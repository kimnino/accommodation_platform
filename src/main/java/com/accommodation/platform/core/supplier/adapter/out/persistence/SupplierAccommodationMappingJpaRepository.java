package com.accommodation.platform.core.supplier.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierAccommodationMappingJpaRepository
        extends JpaRepository<SupplierAccommodationMappingJpaEntity, Long> {

    List<SupplierAccommodationMappingJpaEntity> findBySupplierId(Long supplierId);

    Optional<SupplierAccommodationMappingJpaEntity> findBySupplierIdAndExternalAccommodationId(
            Long supplierId, String externalAccommodationId);
}
