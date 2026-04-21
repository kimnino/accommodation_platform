package com.accommodation.platform.extranet.accommodation.application.port.in;

public interface ExtranetUpdateAccommodationUseCase {

    Long requestModification(Long accommodationId, Long partnerId, UpdateAccommodationCommand command);

    record UpdateAccommodationCommand(
            String name,
            String fullAddress,
            Double latitude,
            Double longitude,
            String locationDescription,
            String checkInTime,
            String checkOutTime
    ) {
    }
}
