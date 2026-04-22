package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetGetAccommodationRegionQuery;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetSetAccommodationRegionUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet")
public class ExtranetAccommodationRegionController {

    private final ExtranetGetAccommodationRegionQuery regionQuery;
    private final ExtranetSetAccommodationRegionUseCase setRegionUseCase;

    @GetMapping("/accommodation-regions")
    public ApiResponse<List<AccommodationRegionResponse>> getRegions(
            @RequestParam AccommodationType accommodationType) {

        List<AccommodationRegion> regions = regionQuery.getByAccommodationType(accommodationType);
        return ApiResponse.success(AccommodationRegionResponse.toTree(regions));
    }

    @PatchMapping("/accommodations/{accommodationId}/region")
    public ApiResponse<AccommodationDetailResponse> setRegion(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestParam Long regionId) {

        Accommodation accommodation = setRegionUseCase.setRegion(accommodationId, partnerId, regionId);
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }
}
