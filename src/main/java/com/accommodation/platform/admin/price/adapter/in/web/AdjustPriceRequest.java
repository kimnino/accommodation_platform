package com.accommodation.platform.admin.price.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.admin.price.application.port.in.AdminAdjustPriceUseCase.AdjustPriceCommand;

public record AdjustPriceRequest(
        @NotNull(message = "시작일은 필수입니다.") LocalDate startDate,
        @NotNull(message = "종료일은 필수입니다.") LocalDate endDate,
        @NotNull(message = "판매 가격은 필수입니다.") BigDecimal sellingPrice,
        boolean taxIncluded
) {

    public AdjustPriceCommand toCommand() {

        return new AdjustPriceCommand(startDate, endDate, sellingPrice, taxIncluded);
    }
}
