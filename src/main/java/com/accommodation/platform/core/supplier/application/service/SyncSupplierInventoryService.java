package com.accommodation.platform.core.supplier.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.core.supplier.application.port.in.SyncSupplierInventoryUseCase;
import com.accommodation.platform.core.supplier.application.port.out.LoadSupplierPort;
import com.accommodation.platform.core.supplier.application.port.out.LoadSupplierPort.AccommodationMapping;
import com.accommodation.platform.core.supplier.application.port.out.LoadSupplierPort.SupplierInfo;
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
    private final LoadSupplierPort loadSupplierPort;
    private final PersistRoomPricePort persistRoomPricePort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final PersistInventoryPort persistInventoryPort;
    private final LoadInventoryPort loadInventoryPort;

    @Override
    public SyncResult syncPricesAndInventory(String supplierCode, LocalDate startDate, LocalDate endDate) {

        SupplierClient client = findClient(supplierCode);

        SupplierInfo supplier = loadSupplierPort.findByCode(supplierCode)
                .orElseThrow(() -> new IllegalArgumentException("공급사를 찾을 수 없습니다: " + supplierCode));

        List<AccommodationMapping> accMappings = loadSupplierPort.findAccommodationMappingsBySupplierId(supplier.id());

        int priceCount = 0;
        int inventoryCount = 0;

        for (AccommodationMapping accMapping : accMappings) {

            List<CanonicalPrice> canonicalPrices = client.fetchPrices(
                    accMapping.externalAccommodationId(), startDate, endDate);

            for (CanonicalPrice cp : canonicalPrices) {

                Long roomOptionId = loadSupplierPort.resolveRoomOptionId(supplier.id(), cp.externalRoomId())
                        .orElse(null);

                if (roomOptionId == null) {
                    log.warn("[{}] 매핑되지 않은 객실: {}", supplierCode, cp.externalRoomId());
                    continue;
                }

                syncPrice(roomOptionId, cp);
                priceCount++;

                syncInventory(roomOptionId, cp);
                inventoryCount++;
            }
        }

        log.info("[{}] 동기화 완료: 숙소 {}개, 가격 {}건, 재고 {}건",
                supplierCode, accMappings.size(), priceCount, inventoryCount);

        return new SyncResult(accMappings.size(), priceCount, inventoryCount);
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

    private SupplierClient findClient(String supplierCode) {

        return supplierClients.stream()
                .filter(c -> c.getSupplierCode().equals(supplierCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("공급사 클라이언트를 찾을 수 없습니다: " + supplierCode));
    }
}
