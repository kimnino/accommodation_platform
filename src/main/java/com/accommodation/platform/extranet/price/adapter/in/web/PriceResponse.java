package com.accommodation.platform.extranet.price.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

public record PriceResponse(
        Long id,
        Long roomOptionId,
        LocalDate date,
        String priceType,
        BigDecimal basePrice,
        BigDecimal sellingPrice,
        boolean taxIncluded
) {

    public static PriceResponse from(RoomPrice price) {

        return new PriceResponse(
                price.getId(),
                price.getRoomOptionId(),
                price.getDate(),
                price.getPriceType().name(),
                price.getBasePrice(),
                price.getSellingPrice(),
                price.isTaxIncluded());
    }
}
