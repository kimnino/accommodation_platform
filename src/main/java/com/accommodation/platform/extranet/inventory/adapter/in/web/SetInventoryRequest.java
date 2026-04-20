package com.accommodation.platform.extranet.inventory.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;

import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetInventoryUseCase.SetInventoryCommand;

public record SetInventoryRequest(
        LocalDate startDate,
        LocalDate endDate,
        List<LocalDate> dates,
        @Min(value = 0, message = "재고는 0 이상이어야 합니다.") int totalQuantity
) {

    public SetInventoryCommand toCommand() {

        return new SetInventoryCommand(startDate, endDate, dates, totalQuantity);
    }
}
