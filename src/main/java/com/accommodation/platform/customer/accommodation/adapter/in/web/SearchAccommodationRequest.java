package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.SearchCriteria;

public record SearchAccommodationRequest(
        Long regionId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer guestCount,
        String accommodationType,
        Long minPrice,
        Long maxPrice,
        List<Long> tagIds,
        String sort
) {

    public SearchCriteria toCriteria() {

        return new SearchCriteria(
                regionId, checkInDate, checkOutDate, guestCount != null ? guestCount : 0,
                accommodationType, minPrice, maxPrice, tagIds, sort);
    }
}
