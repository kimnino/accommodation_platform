package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery.SearchCriteria;

public record SearchAccommodationRequest(
        String region,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guestCount,
        String accommodationType,
        Long minPrice,
        Long maxPrice,
        List<Long> tagIds,
        String sort
) {

    public SearchCriteria toCriteria() {

        return new SearchCriteria(
                region, checkInDate, checkOutDate, guestCount,
                accommodationType, minPrice, maxPrice, tagIds, sort);
    }
}
