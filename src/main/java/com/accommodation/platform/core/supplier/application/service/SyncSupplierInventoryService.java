package com.accommodation.platform.core.supplier.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierAccommodationMappingJpaEntity;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierAccommodationMappingJpaRepository;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierJpaEntity;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierJpaRepository;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierRoomMappingJpaEntity;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierRoomMappingJpaRepository;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierRoomOptionMappingJpaEntity;
import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierRoomOptionMappingJpaRepository;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.supplier.application.port.in.SyncSupplierInventoryUseCase;
import com.accommodation.platform.core.supplier.application.port.out.SupplierClient;
import com.accommodation.platform.core.supplier.domain.model.CanonicalPrice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SyncSupplierInventoryService implements SyncSupplierInventoryUseCase {

    private final List<SupplierClient> supplierClients;
    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierAccommodationMappingJpaRepository accommodationMappingRepository;
    private final SupplierRoomMappingJpaRepository roomMappingRepository;
    private final SupplierRoomOptionMappingJpaRepository roomOptionMappingRepository;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final PersistRoomPricePort persistRoomPricePort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final PersistInventoryPort persistInventoryPort;
    private final LoadInventoryPort loadInventoryPort;

    @Override
    public SyncResult syncPricesAndInventory(String supplierCode, LocalDate startDate, LocalDate endDate) {

        SupplierClient client = findClient(supplierCode);

        SupplierJpaEntity supplier = supplierJpaRepository.findByCode(supplierCode)
                .orElseThrow(() -> new IllegalArgumentException("공급사를 찾을 수 없습니다: " + supplierCode));

        List<SupplierAccommodationMappingJpaEntity> accMappings =
                accommodationMappingRepository.findBySupplierId(supplier.getId());

        AtomicInteger priceCount = new AtomicInteger(0);
        AtomicInteger inventoryCount = new AtomicInteger(0);

        for (SupplierAccommodationMappingJpaEntity accMapping : accMappings) {

            List<CanonicalPrice> canonicalPrices = client.fetchPrices(
                    accMapping.getExternalAccommodationId(), startDate, endDate);

            for (CanonicalPrice cp : canonicalPrices) {

                Long roomOptionId = resolveRoomOptionId(supplier.getId(), cp.externalRoomId());

                if (roomOptionId == null) {
                    log.warn("[{}] 매핑되지 않은 객실: {}", supplierCode, cp.externalRoomId());
                    continue;
                }

                syncPrice(roomOptionId, cp);
                priceCount.incrementAndGet();

                syncInventory(roomOptionId, cp);
                inventoryCount.incrementAndGet();
            }
        }

        log.info("[{}] 동기화 완료: 숙소 {}개, 가격 {}건, 재고 {}건",
                supplierCode, accMappings.size(), priceCount.get(), inventoryCount.get());

        return new SyncResult(accMappings.size(), priceCount.get(), inventoryCount.get());
    }

    private void syncPrice(Long roomOptionId, CanonicalPrice cp) {

        List<RoomPrice> existing = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                roomOptionId, PriceType.STAY, cp.date(), cp.date());

        if (!existing.isEmpty()) {
            RoomPrice price = existing.getFirst();
            price.updatePrice(cp.basePrice(), cp.sellingPrice(), cp.taxIncluded());
            persistRoomPricePort.save(price);
        } else {
            RoomPrice price = RoomPrice.builder()
                    .roomOptionId(roomOptionId)
                    .date(cp.date())
                    .priceType(PriceType.STAY)
                    .basePrice(cp.basePrice())
                    .sellingPrice(cp.sellingPrice())
                    .taxIncluded(cp.taxIncluded())
                    .build();
            persistRoomPricePort.save(price);
        }
    }

    private void syncInventory(Long roomOptionId, CanonicalPrice cp) {

        List<Inventory> existing = loadInventoryPort.findByRoomOptionIdAndDateRange(
                roomOptionId, cp.date(), cp.date());

        if (!existing.isEmpty()) {
            Inventory inventory = existing.getFirst();
            inventory.updateTotalQuantity(cp.availableQuantity());
            persistInventoryPort.save(inventory);
        } else {
            Inventory inventory = Inventory.builder()
                    .roomOptionId(roomOptionId)
                    .date(cp.date())
                    .totalQuantity(cp.availableQuantity())
                    .remainingQuantity(cp.availableQuantity())
                    .build();
            persistInventoryPort.save(inventory);
        }
    }

    /**
     * 외부 객실 ID → 내부 roomOptionId 해결.
     * 1) 3단 매핑(supplier_room_option_mapping) 먼저 확인
     * 2) 없으면 2단 매핑(supplier_room_mapping)으로 roomId 찾고, 해당 객실의 첫 번째 옵션으로 자동 연결
     */
    private Long resolveRoomOptionId(Long supplierId, String externalRoomId) {

        return roomOptionMappingRepository
                .findBySupplierIdAndExternalRoomOptionId(supplierId, externalRoomId)
                .map(SupplierRoomOptionMappingJpaEntity::getRoomOptionId)
                .orElseGet(() -> {
                    SupplierRoomMappingJpaEntity roomMapping = roomMappingRepository
                            .findBySupplierIdAndExternalRoomId(supplierId, externalRoomId)
                            .orElse(null);

                    if (roomMapping == null) {
                        return null;
                    }

                    return loadRoomOptionPort.findByRoomId(roomMapping.getRoomId())
                            .stream()
                            .findFirst()
                            .map(opt -> opt.getId())
                            .orElse(null);
                });
    }

    private SupplierClient findClient(String supplierCode) {

        return supplierClients.stream()
                .filter(c -> c.getSupplierCode().equals(supplierCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("공급사 클라이언트를 찾을 수 없습니다: " + supplierCode));
    }
}
