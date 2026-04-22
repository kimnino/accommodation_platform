package com.accommodation.platform.admin.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationTranslation;

public interface AdminListAccommodationQuery {

    List<Accommodation> listAll();

    List<Accommodation> listByStatus(AccommodationStatus status);

    Accommodation getById(Long accommodationId);

    AccommodationDetail getDetailById(Long accommodationId);

    record AccommodationDetail(Accommodation accommodation, List<AccommodationTranslation> translations) {
    }
}
