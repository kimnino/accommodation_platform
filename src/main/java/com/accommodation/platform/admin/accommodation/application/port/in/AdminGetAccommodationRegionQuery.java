package com.accommodation.platform.admin.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

public interface AdminGetAccommodationRegionQuery {

    List<AccommodationRegion> getByAccommodationType(AccommodationType type);

    List<AccommodationRegion> getAll();
}
