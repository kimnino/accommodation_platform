package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.accommodation.domain.model.AccommodationTranslation;

public interface LoadAccommodationTranslationPort {

    List<AccommodationTranslation> findByAccommodationId(Long accommodationId);

    Optional<AccommodationTranslation> findByAccommodationIdAndLocale(Long accommodationId, String locale);

    List<AccommodationTranslation> findByAccommodationIdInAndLocale(List<Long> accommodationIds, String locale);
}
