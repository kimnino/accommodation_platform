package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.Optional;

import com.accommodation.platform.core.accommodation.domain.model.AccommodationHourlySetting;

public interface LoadHourlySettingPort {
    Optional<AccommodationHourlySetting> findByAccommodationId(Long accommodationId);
}
