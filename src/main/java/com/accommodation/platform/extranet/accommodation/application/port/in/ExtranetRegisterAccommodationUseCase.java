package com.accommodation.platform.extranet.accommodation.application.port.in;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface ExtranetRegisterAccommodationUseCase {

    Accommodation register(RegisterAccommodationCommand command);

    record RegisterAccommodationCommand(
            Long partnerId,
            String name,
            String type,
            String fullAddress,
            double latitude,
            double longitude,
            String locationDescription,
            String checkInTime,
            String checkOutTime
    ) {
    }
}
