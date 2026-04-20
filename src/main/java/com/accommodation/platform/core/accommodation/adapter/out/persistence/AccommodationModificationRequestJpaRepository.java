package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accommodation.platform.core.accommodation.domain.enums.ModificationStatus;

public interface AccommodationModificationRequestJpaRepository
        extends JpaRepository<AccommodationModificationRequestJpaEntity, Long> {

    List<AccommodationModificationRequestJpaEntity> findByStatus(ModificationStatus status);

    List<AccommodationModificationRequestJpaEntity> findByAccommodationId(Long accommodationId);
}
