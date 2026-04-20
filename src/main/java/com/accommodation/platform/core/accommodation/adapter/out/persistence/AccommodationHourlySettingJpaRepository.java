package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationHourlySettingJpaRepository
        extends JpaRepository<AccommodationHourlySettingJpaEntity, Long> {

    Optional<AccommodationHourlySettingJpaEntity> findByAccommodationId(Long accommodationId);
}
