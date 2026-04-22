package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

public interface LoadAccommodationRegionPort {

    Optional<AccommodationRegion> findById(Long id);

    List<AccommodationRegion> findByAccommodationType(AccommodationType type);

    List<AccommodationRegion> findAll();
}
