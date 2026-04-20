package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetGetAccommodationQuery;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetUpdateAccommodationUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet/accommodations")
public class ExtranetAccommodationController {

    private final ExtranetRegisterAccommodationUseCase registerUseCase;
    private final ExtranetUpdateAccommodationUseCase updateUseCase;
    private final ExtranetGetAccommodationQuery getQuery;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AccommodationDetailResponse> register(
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody RegisterAccommodationRequest request) {

        Accommodation accommodation = registerUseCase.register(request.toCommand(partnerId));
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }

    @PutMapping("/{accommodationId}")
    public ApiResponse<Long> requestModification(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody UpdateAccommodationRequest request) {

        Long modificationRequestId = updateUseCase.requestModification(
                accommodationId, partnerId, request.toCommand());
        return ApiResponse.success(modificationRequestId);
    }

    @GetMapping("/{accommodationId}")
    public ApiResponse<AccommodationDetailResponse> getById(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        Accommodation accommodation = getQuery.getById(accommodationId, partnerId);
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }

    @GetMapping
    public ApiResponse<List<AccommodationDetailResponse>> getMyAccommodations(
            @RequestHeader("X-Partner-Id") Long partnerId) {

        List<AccommodationDetailResponse> responses = getQuery.getByPartnerId(partnerId).stream()
                .map(AccommodationDetailResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
