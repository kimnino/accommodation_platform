package com.accommodation.platform.core.accommodation.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery.AccommodationSummary;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery.SearchCriteria;

public interface SearchAccommodationPort {

    Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable);
}
