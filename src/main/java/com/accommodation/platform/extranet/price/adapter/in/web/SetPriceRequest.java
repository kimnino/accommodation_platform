package com.accommodation.platform.extranet.price.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.extranet.price.application.port.in.ExtranetSetPriceUseCase.SetPriceCommand;

public record SetPriceRequest(
        LocalDate startDate,
        LocalDate endDate,
        List<LocalDate> dates,
        @NotNull(message = "가격 유형은 필수입니다. (STAY 또는 HOURLY)") PriceType priceType,
        @NotNull(message = "기본 가격은 필수입니다.") BigDecimal basePrice,
        @NotNull(message = "판매 가격은 필수입니다.") BigDecimal sellingPrice,
        boolean taxIncluded
) {

    public SetPriceCommand toCommand() {

        return new SetPriceCommand(startDate, endDate, dates, priceType, basePrice, sellingPrice, taxIncluded);
    }
}
