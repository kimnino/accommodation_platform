package com.accommodation.platform.extranet.accommodation.application.port.in;

import java.util.List;

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
            String checkOutTime,
            List<String> supportedLocales,
            List<TranslationCommand> translations
    ) {
    }

    record TranslationCommand(
            String locale,
            String name,
            String fullAddress,
            String locationDescription,
            String introduction,
            String serviceAndFacilities,
            String usageInfo,
            String cancellationAndRefundPolicy
    ) {
    }
}
