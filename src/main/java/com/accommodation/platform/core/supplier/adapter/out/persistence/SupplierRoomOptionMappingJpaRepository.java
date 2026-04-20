package com.accommodation.platform.core.supplier.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRoomOptionMappingJpaRepository
        extends JpaRepository<SupplierRoomOptionMappingJpaEntity, Long> {

    Optional<SupplierRoomOptionMappingJpaEntity> findBySupplierIdAndExternalRoomOptionId(
            Long supplierId, String externalRoomOptionId);
}
