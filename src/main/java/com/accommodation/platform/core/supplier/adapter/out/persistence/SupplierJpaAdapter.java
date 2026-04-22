package com.accommodation.platform.core.supplier.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.supplier.application.port.out.LoadSupplierPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SupplierJpaAdapter implements LoadSupplierPort {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierAccommodationMappingJpaRepository accommodationMappingRepository;
    private final SupplierRoomMappingJpaRepository roomMappingRepository;
    private final SupplierRoomOptionMappingJpaRepository roomOptionMappingRepository;
    private final LoadRoomOptionPort loadRoomOptionPort;

    @Override
    public Optional<SupplierInfo> findByCode(String code) {

        return supplierJpaRepository.findByCode(code)
                .map(e -> new SupplierInfo(e.getId(), e.getName(), e.getCode()));
    }

    @Override
    public List<AccommodationMapping> findAccommodationMappingsBySupplierId(Long supplierId) {

        return accommodationMappingRepository.findBySupplierId(supplierId).stream()
                .map(e -> new AccommodationMapping(e.getAccommodationId(), e.getExternalAccommodationId()))
                .toList();
    }

    @Override
    public Optional<Long> resolveRoomOptionId(Long supplierId, String externalRoomId) {

        return roomOptionMappingRepository
                .findBySupplierIdAndExternalRoomOptionId(supplierId, externalRoomId)
                .map(SupplierRoomOptionMappingJpaEntity::getRoomOptionId)
                .or(() -> roomMappingRepository
                        .findBySupplierIdAndExternalRoomId(supplierId, externalRoomId)
                        .flatMap(roomMapping -> loadRoomOptionPort.findByRoomId(roomMapping.getRoomId())
                                .stream()
                                .findFirst()
                                .map(opt -> opt.getId())));
    }
}
