package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetUpdateAccommodationUseCase.UpdateAccommodationCommand;

public record UpdateAccommodationRequest(
        String name,
        String fullAddress,
        Double latitude,
        Double longitude,
        String locationDescription,
        String checkInTime,
        String checkOutTime
) {

    public UpdateAccommodationCommand toCommand() {

        return new UpdateAccommodationCommand(
                name, fullAddress, latitude, longitude,
                locationDescription, checkInTime, checkOutTime);
    }
}
