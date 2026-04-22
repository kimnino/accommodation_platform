package com.accommodation.platform.core.accommodation.application.port.out;

import com.accommodation.platform.core.accommodation.domain.model.AccommodationHourlySetting;

public interface PersistHourlySettingPort {
    AccommodationHourlySetting save(AccommodationHourlySetting setting);
    void deleteByAccommodationId(Long accommodationId);
}
