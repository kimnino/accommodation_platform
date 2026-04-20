package com.accommodation.platform.extranet.inventory.adapter.in.web;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetTimeSlotInventoryUseCase.OpenTimeSlotsCommand;

public record OpenTimeSlotsRequest(
        @NotNull(message = "시작일은 필수입니다.") LocalDate startDate,
        @NotNull(message = "종료일은 필수입니다.") LocalDate endDate,
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.") int totalQuantity
) {

    public OpenTimeSlotsCommand toCommand() {

        return new OpenTimeSlotsCommand(startDate, endDate, totalQuantity);
    }
}
