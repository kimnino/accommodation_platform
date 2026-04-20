package com.accommodation.platform.customer.accommodation.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerSearchAccommodationService implements CustomerSearchAccommodationQuery {

    private final SearchAccommodationPort searchAccommodationPort;

    @Override
    public Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable) {

        return searchAccommodationPort.search(criteria, pageable);
    }
}
