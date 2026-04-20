package com.accommodation.platform.admin.accommodation.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminApproveAccommodationUseCase;
import com.accommodation.platform.admin.accommodation.application.port.in.AdminApproveModificationUseCase;
import com.accommodation.platform.admin.accommodation.application.port.in.AdminListAccommodationQuery;
import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.adapter.in.web.AccommodationDetailResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/accommodations")
public class AdminAccommodationController {

    private final AdminApproveAccommodationUseCase approveUseCase;
    private final AdminApproveModificationUseCase modificationUseCase;
    private final AdminListAccommodationQuery listQuery;

    @GetMapping
    public ApiResponse<List<AccommodationDetailResponse>> listAll() {

        List<AccommodationDetailResponse> responses = listQuery.listAll().stream()
                .map(AccommodationDetailResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{accommodationId}")
    public ApiResponse<AccommodationDetailResponse> getById(@PathVariable Long accommodationId) {

        Accommodation accommodation = listQuery.getById(accommodationId);
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }

    @PatchMapping("/{accommodationId}/approve")
    public ApiResponse<AccommodationDetailResponse> approve(@PathVariable Long accommodationId) {

        Accommodation accommodation = approveUseCase.approve(accommodationId);
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }

    @PatchMapping("/{accommodationId}/suspend")
    public ApiResponse<AccommodationDetailResponse> suspend(@PathVariable Long accommodationId) {

        Accommodation accommodation = approveUseCase.suspend(accommodationId);
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }

    @PatchMapping("/{accommodationId}/close")
    public ApiResponse<AccommodationDetailResponse> close(@PathVariable Long accommodationId) {

        Accommodation accommodation = approveUseCase.close(accommodationId);
        return ApiResponse.success(AccommodationDetailResponse.from(accommodation));
    }

    @PatchMapping("/modifications/{modificationRequestId}/approve")
    public ApiResponse<Void> approveModification(@PathVariable Long modificationRequestId) {

        modificationUseCase.approve(modificationRequestId);
        return ApiResponse.success(null);
    }

    @PatchMapping("/modifications/{modificationRequestId}/reject")
    public ApiResponse<Void> rejectModification(
            @PathVariable Long modificationRequestId,
            @RequestBody RejectModificationRequest request) {

        modificationUseCase.reject(modificationRequestId, request.reason());
        return ApiResponse.success(null);
    }

    public record RejectModificationRequest(String reason) {
    }
}
