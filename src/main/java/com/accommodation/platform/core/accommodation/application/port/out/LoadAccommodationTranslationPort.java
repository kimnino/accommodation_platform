package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;

public interface LoadAccommodationTranslationPort {

    List<AccommodationTranslationJpaEntity> findByAccommodationId(Long accommodationId);

    Optional<AccommodationTranslationJpaEntity> findByAccommodationIdAndLocale(Long accommodationId, String locale);

    List<AccommodationTranslationJpaEntity> findByAccommodationIdInAndLocale(List<Long> accommodationIds, String locale);
}
