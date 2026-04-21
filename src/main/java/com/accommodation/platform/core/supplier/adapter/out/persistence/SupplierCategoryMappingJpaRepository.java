package com.accommodation.platform.core.supplier.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accommodation.platform.core.supplier.adapter.out.persistence.SupplierCategoryMappingJpaEntity.MappingStatus;

public interface SupplierCategoryMappingJpaRepository extends JpaRepository<SupplierCategoryMappingJpaEntity, Long> {

    Optional<SupplierCategoryMappingJpaEntity> findBySupplierIdAndSupplierGroupAndSupplierValue(
            Long supplierId, String supplierGroup, String supplierValue);

    List<SupplierCategoryMappingJpaEntity> findBySupplierIdAndMappingStatus(
            Long supplierId, MappingStatus mappingStatus);

    List<SupplierCategoryMappingJpaEntity> findBySupplierId(Long supplierId);
}
