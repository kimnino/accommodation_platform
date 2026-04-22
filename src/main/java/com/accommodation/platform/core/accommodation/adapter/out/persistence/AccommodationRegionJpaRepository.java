package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

interface AccommodationRegionJpaRepository extends JpaRepository<AccommodationRegionJpaEntity, Long> {

    List<AccommodationRegionJpaEntity> findByAccommodationTypeOrderBySortOrderAsc(AccommodationType type);
}
