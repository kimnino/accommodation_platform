package com.accommodation.platform.core.supplier.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRoomMappingJpaRepository extends JpaRepository<SupplierRoomMappingJpaEntity, Long> {

    Optional<SupplierRoomMappingJpaEntity> findBySupplierIdAndExternalRoomId(Long supplierId, String externalRoomId);
}
