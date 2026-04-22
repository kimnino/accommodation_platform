package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;

public interface AccommodationJpaRepository extends JpaRepository<AccommodationJpaEntity, Long> {

    List<AccommodationJpaEntity> findByPartnerId(Long partnerId);

    List<AccommodationJpaEntity> findByStatus(AccommodationStatus status);
}
