package com.accommodation.platform.admin.accommodation.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminGetAccommodationRegionQuery;
import com.accommodation.platform.admin.accommodation.application.port.in.AdminManageAccommodationRegionUseCase;
import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/accommodation-regions")
public class AdminAccommodationRegionController {

    private final AdminManageAccommodationRegionUseCase manageUseCase;
    private final AdminGetAccommodationRegionQuery regionQuery;

    @GetMapping
    public ApiResponse<List<AccommodationRegionResponse>> getTree(
            @RequestParam AccommodationType accommodationType) {

        List<AccommodationRegion> regions = regionQuery.getByAccommodationType(accommodationType);
        return ApiResponse.success(AccommodationRegionResponse.toTree(regions));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AccommodationRegionResponse> create(
            @Valid @RequestBody CreateRegionRequest request) {

        AccommodationRegion region = manageUseCase.create(request.toCommand());
        return ApiResponse.success(AccommodationRegionResponse.from(region));
    }

    @PutMapping("/{regionId}")
    public ApiResponse<AccommodationRegionResponse> update(
            @PathVariable Long regionId,
            @Valid @RequestBody UpdateRegionRequest request) {

        AccommodationRegion region = manageUseCase.update(regionId, request.toCommand());
        return ApiResponse.success(AccommodationRegionResponse.from(region));
    }

    @DeleteMapping("/{regionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long regionId) {

        manageUseCase.delete(regionId);
    }
}
