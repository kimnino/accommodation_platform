package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationJpaRepository extends JpaRepository<AccommodationJpaEntity, Long> {

    List<AccommodationJpaEntity> findByPartnerId(Long partnerId);
}
