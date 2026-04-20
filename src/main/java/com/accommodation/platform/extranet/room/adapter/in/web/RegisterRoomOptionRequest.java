package com.accommodation.platform.extranet.room.adapter.in.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomOptionUseCase.RegisterRoomOptionCommand;

public record RegisterRoomOptionRequest(
        @NotBlank(message = "옵션명은 필수입니다.") String name,
        @NotBlank(message = "취소 정책은 필수입니다.") String cancellationPolicy,
        BigDecimal additionalPrice
) {

    public RegisterRoomOptionCommand toCommand() {

        return new RegisterRoomOptionCommand(name, cancellationPolicy, additionalPrice);
    }
}
