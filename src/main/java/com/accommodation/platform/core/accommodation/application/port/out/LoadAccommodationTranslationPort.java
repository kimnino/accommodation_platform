package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;

import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;

public interface LoadAccommodationTranslationPort {

    List<AccommodationTranslationJpaEntity> findByAccommodationId(Long accommodationId);
}
