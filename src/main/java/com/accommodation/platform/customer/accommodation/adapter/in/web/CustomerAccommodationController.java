package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.AccommodationDetail;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery.AccommodationSummary;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accommodations")
public class CustomerAccommodationController {

    private final CustomerSearchAccommodationQuery searchQuery;
    private final CustomerGetAccommodationDetailQuery detailQuery;

    @GetMapping
    public ApiResponse<Page<AccommodationSummary>> search(
            SearchAccommodationRequest request,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<AccommodationSummary> result = searchQuery.search(request.toCriteria(), pageable);
        return ApiResponse.success(result);
    }

    @GetMapping("/{accommodationId}")
    public ApiResponse<AccommodationDetail> getDetail(
            @PathVariable Long accommodationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {

        AccommodationDetail detail = detailQuery.getDetail(accommodationId, checkInDate, checkOutDate);
        return ApiResponse.success(detail);
    }
}
