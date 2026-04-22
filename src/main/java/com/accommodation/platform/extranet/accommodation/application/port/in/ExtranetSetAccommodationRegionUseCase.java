package com.accommodation.platform.extranet.accommodation.application.port.in;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface ExtranetSetAccommodationRegionUseCase {

    Accommodation setRegion(Long accommodationId, Long partnerId, Long regionId);
}
