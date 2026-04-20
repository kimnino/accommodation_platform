package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase.RegisterAccommodationCommand;

public record RegisterAccommodationRequest(
        @NotBlank(message = "숙소명은 필수입니다.") String name,
        @NotBlank(message = "숙소 유형은 필수입니다.") String type,
        @NotBlank(message = "주소는 필수입니다.") String fullAddress,
        @NotNull(message = "위도는 필수입니다.") Double latitude,
        @NotNull(message = "경도는 필수입니다.") Double longitude,
        String locationDescription,
        String checkInTime,
        String checkOutTime
) {

    public RegisterAccommodationCommand toCommand(Long partnerId) {

        return new RegisterAccommodationCommand(
                partnerId, name, type, fullAddress,
                latitude, longitude, locationDescription,
                checkInTime, checkOutTime);
    }
}
