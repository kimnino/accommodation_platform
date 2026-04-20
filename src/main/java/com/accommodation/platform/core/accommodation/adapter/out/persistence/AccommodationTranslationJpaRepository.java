package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationTranslationJpaRepository extends JpaRepository<AccommodationTranslationJpaEntity, Long> {

    List<AccommodationTranslationJpaEntity> findByAccommodationId(Long accommodationId);

    void deleteByAccommodationId(Long accommodationId);
}
