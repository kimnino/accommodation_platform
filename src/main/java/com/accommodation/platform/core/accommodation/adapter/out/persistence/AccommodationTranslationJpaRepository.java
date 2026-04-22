package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationTranslationJpaRepository extends JpaRepository<AccommodationTranslationJpaEntity, Long> {

    List<AccommodationTranslationJpaEntity> findByAccommodationId(Long accommodationId);

    Optional<AccommodationTranslationJpaEntity> findByAccommodationIdAndLocale(Long accommodationId, String locale);

    List<AccommodationTranslationJpaEntity> findByAccommodationIdInAndLocale(List<Long> accommodationIds, String locale);

    void deleteByAccommodationId(Long accommodationId);
}
