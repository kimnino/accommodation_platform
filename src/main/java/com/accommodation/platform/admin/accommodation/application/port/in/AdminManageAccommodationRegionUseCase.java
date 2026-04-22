package com.accommodation.platform.admin.accommodation.application.port.in;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

public interface AdminManageAccommodationRegionUseCase {

    AccommodationRegion create(CreateRegionCommand command);

    AccommodationRegion update(Long id, UpdateRegionCommand command);

    void delete(Long id);

    record CreateRegionCommand(
            AccommodationType accommodationType,
            String regionName,
            Long parentId,
            int sortOrder
    ) {}

    record UpdateRegionCommand(
            String regionName,
            Long parentId,
            int sortOrder
    ) {}
}
