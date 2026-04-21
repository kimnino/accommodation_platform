package com.accommodation.platform.admin.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface AdminListAccommodationQuery {

    List<Accommodation> listAll();

    Accommodation getById(Long accommodationId);

    AccommodationDetail getDetailById(Long accommodationId);

    record AccommodationDetail(Accommodation accommodation, List<AccommodationTranslationJpaEntity> translations) {
    }
}
