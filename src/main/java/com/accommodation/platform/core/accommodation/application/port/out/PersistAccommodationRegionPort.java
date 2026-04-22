package com.accommodation.platform.core.accommodation.application.port.out;

import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

public interface PersistAccommodationRegionPort {

    AccommodationRegion save(AccommodationRegion region);

    void delete(Long id);
}
