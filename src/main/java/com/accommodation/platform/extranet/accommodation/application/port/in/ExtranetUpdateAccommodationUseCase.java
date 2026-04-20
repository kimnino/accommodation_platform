package com.accommodation.platform.extranet.accommodation.application.port.in;

public interface ExtranetUpdateAccommodationUseCase {

    Long requestModification(Long accommodationId, Long partnerId, UpdateAccommodationCommand command);

    record UpdateAccommodationCommand(
            String name,
            String fullAddress,
            double latitude,
            double longitude,
            String locationDescription,
            String checkInTime,
            String checkOutTime
    ) {
    }
}
