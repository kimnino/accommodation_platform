package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;

import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;

public interface PersistAccommodationTranslationPort {

    void saveAll(List<AccommodationTranslationJpaEntity> translations);

    void deleteByAccommodationId(Long accommodationId);
}
