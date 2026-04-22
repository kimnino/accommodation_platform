package com.accommodation.platform.extranet.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

public interface ExtranetGetAccommodationRegionQuery {

    List<AccommodationRegion> getByAccommodationType(AccommodationType type);
}
