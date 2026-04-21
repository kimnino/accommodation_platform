package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import java.util.List;

import com.accommodation.platform.extranet.accommodation.adapter.in.web.RegisterAccommodationRequest.TranslationRequest;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase.TranslationCommand;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetUpdateAccommodationUseCase.UpdateAccommodationCommand;

public record UpdateAccommodationRequest(
        String name,
        String fullAddress,
        Double latitude,
        Double longitude,
        String locationDescription,
        String checkInTime,
        String checkOutTime,
        List<TranslationRequest> translations
) {

    public UpdateAccommodationCommand toCommand() {

        List<TranslationCommand> translationCommands = translations != null
                ? translations.stream().map(TranslationRequest::toCommand).toList()
                : List.of();

        return new UpdateAccommodationCommand(
                name, fullAddress, latitude, longitude,
                locationDescription, checkInTime, checkOutTime,
                translationCommands);
    }
}
