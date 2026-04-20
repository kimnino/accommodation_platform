package com.accommodation.platform.extranet.inventory.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetHourlySettingUseCase.SetHourlySettingCommand;

public record SetHourlySettingRequest(
        @NotBlank(message = "운영 시작 시간은 필수입니다.") String operatingStartTime,
        @NotBlank(message = "운영 종료 시간은 필수입니다.") String operatingEndTime,
        @Min(value = 30, message = "이용 시간은 최소 30분입니다.") int usageDurationMinutes,
        @Min(value = 0, message = "버퍼 시간은 0 이상이어야 합니다.") int bufferMinutes,
        @NotNull(message = "슬롯 단위는 필수입니다. (30 또는 60)") Integer slotUnitMinutes
) {

    public SetHourlySettingCommand toCommand() {

        return new SetHourlySettingCommand(
                operatingStartTime, operatingEndTime,
                usageDurationMinutes, bufferMinutes, slotUnitMinutes);
    }
}
