package com.accommodation.platform.customer.accommodation.application.port.in;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerSearchAccommodationQuery {

    Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable);

    record SearchCriteria(String region, LocalDate checkInDate, LocalDate checkOutDate, int guestCount,
                          String accommodationType, Long minPrice, Long maxPrice, List<Long> tagIds, String sort) {
    }

    record AccommodationSummary(Long id, String name, String type, String fullAddress, double latitude,
                                double longitude, String primaryImagePath, Long lowestPrice, boolean hasAvailableRoom) {
    }
}
