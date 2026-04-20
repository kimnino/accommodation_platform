package com.accommodation.platform.extranet.room.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomUseCase.RegisterRoomCommand;

public record RegisterRoomRequest(
        @NotBlank(message = "객실명은 필수입니다.") String name,
        String roomTypeName,
        @Min(value = 1, message = "기준 인원은 1명 이상이어야 합니다.") int standardCapacity,
        @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다.") int maxCapacity
) {

    public RegisterRoomCommand toCommand() {

        return new RegisterRoomCommand(name, roomTypeName, standardCapacity, maxCapacity);
    }
}
