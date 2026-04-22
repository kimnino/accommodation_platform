package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetRegionsQuery;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetRegionsQuery.RegionNode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/regions")
public class CustomerRegionController {

    private final CustomerGetRegionsQuery getRegionsQuery;

    @GetMapping
    public ApiResponse<List<RegionNode>> getRegions(
            @RequestParam(required = false) AccommodationType accommodationType) {

        return ApiResponse.success(getRegionsQuery.getRegions(accommodationType));
    }
}
