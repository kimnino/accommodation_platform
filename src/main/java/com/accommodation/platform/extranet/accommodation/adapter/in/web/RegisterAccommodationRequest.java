package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase.RegisterAccommodationCommand;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase.TranslationCommand;

public record RegisterAccommodationRequest(
        @NotBlank(message = "숙소명은 필수입니다.") String name,
        @NotBlank(message = "숙소 유형은 필수입니다.") String type,
        @NotBlank(message = "주소는 필수입니다.") String fullAddress,
        @NotNull(message = "위도는 필수입니다.") Double latitude,
        @NotNull(message = "경도는 필수입니다.") Double longitude,
        String locationDescription,
        String checkInTime,
        String checkOutTime,
        List<String> supportedLocales,
        List<TranslationRequest> translations
) {

    public RegisterAccommodationCommand toCommand(Long partnerId) {

        List<TranslationCommand> translationCommands = translations != null
                ? translations.stream().map(TranslationRequest::toCommand).toList()
                : List.of();

        return new RegisterAccommodationCommand(
                partnerId, name, type, fullAddress,
                latitude, longitude, locationDescription,
                checkInTime, checkOutTime,
                supportedLocales, translationCommands);
    }

    public record TranslationRequest(
            @NotBlank(message = "언어 코드는 필수입니다.") String locale,
            String name,
            String fullAddress,
            String locationDescription,
            String introduction,
            String serviceAndFacilities,
            String usageInfo,
            String cancellationAndRefundPolicy
    ) {

        public TranslationCommand toCommand() {

            return new TranslationCommand(
                    locale, name, fullAddress, locationDescription,
                    introduction, serviceAndFacilities, usageInfo,
                    cancellationAndRefundPolicy);
        }
    }
}
