package com.accommodation.platform.admin.accommodation.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminManageAccommodationRegionUseCase.CreateRegionCommand;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

public record CreateRegionRequest(
        @NotNull AccommodationType accommodationType,
        @NotBlank String regionName,
        Long parentId,
        int sortOrder
) {

    public CreateRegionCommand toCommand() {
        return new CreateRegionCommand(accommodationType, regionName, parentId, sortOrder);
    }
}
