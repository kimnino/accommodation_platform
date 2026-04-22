package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

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
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.AccommodationSummary;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.SearchCriteria;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.AccommodationDetail;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accommodations")
public class CustomerAccommodationController {

    private final CustomerSearchAccommodationQuery searchQuery;
    private final CustomerGetAccommodationDetailQuery detailQuery;

    @GetMapping
    public ApiResponse<Page<AccommodationSummary>> search(
            @RequestParam(name = "region_id", required = false) Long regionId,
            @RequestParam(name = "check_in_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(name = "check_out_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(name = "guest_count", required = false) Integer guestCount,
            @RequestParam(name = "accommodation_type", required = false) String accommodationType,
            @RequestParam(name = "min_price", required = false) Long minPrice,
            @RequestParam(name = "max_price", required = false) Long maxPrice,
            @RequestParam(name = "tag_ids", required = false) List<Long> tagIds,
            @RequestParam(required = false) String sort,
            @PageableDefault(size = 20) Pageable pageable) {

        SearchCriteria criteria = new SearchCriteria(
                regionId, checkInDate, checkOutDate, guestCount != null ? guestCount : 0,
                accommodationType, minPrice, maxPrice, tagIds, sort);
        Page<AccommodationSummary> result = searchQuery.search(criteria, pageable);
        return ApiResponse.success(result);
    }

    @GetMapping("/{accommodationId}")
    public ApiResponse<AccommodationDetail> getDetail(
            @PathVariable Long accommodationId,
            @RequestParam("check_in_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("check_out_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {

        AccommodationDetail detail = detailQuery.getDetail(accommodationId, checkInDate, checkOutDate);
        return ApiResponse.success(detail);
    }
}
