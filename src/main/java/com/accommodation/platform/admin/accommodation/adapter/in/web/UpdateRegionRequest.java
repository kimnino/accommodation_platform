package com.accommodation.platform.admin.accommodation.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminManageAccommodationRegionUseCase.UpdateRegionCommand;

public record UpdateRegionRequest(
        @NotBlank String regionName,
        Long parentId,
        int sortOrder
) {

    public UpdateRegionCommand toCommand() {
        return new UpdateRegionCommand(regionName, parentId, sortOrder);
    }
}
