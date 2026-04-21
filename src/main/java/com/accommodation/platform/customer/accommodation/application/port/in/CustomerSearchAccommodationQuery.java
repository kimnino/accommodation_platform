package com.accommodation.platform.customer.accommodation.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.AccommodationSummary;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.SearchCriteria;

public interface CustomerSearchAccommodationQuery {

    Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable);
}
