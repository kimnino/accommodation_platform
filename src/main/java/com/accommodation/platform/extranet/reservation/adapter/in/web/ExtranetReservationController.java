package com.accommodation.platform.extranet.reservation.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.adapter.in.web.ReservationResponse;
import com.accommodation.platform.extranet.reservation.application.port.in.ExtranetCancelReservationUseCase;
import com.accommodation.platform.extranet.reservation.application.port.in.ExtranetConfirmReservationUseCase;
import com.accommodation.platform.extranet.reservation.application.port.in.ExtranetGetReservationQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet/reservations")
public class ExtranetReservationController {

    private final ExtranetConfirmReservationUseCase confirmUseCase;
    private final ExtranetCancelReservationUseCase cancelUseCase;
    private final ExtranetGetReservationQuery getQuery;

    @PatchMapping("/{reservationId}/confirm")
    public ApiResponse<ReservationResponse> confirm(@PathVariable Long reservationId) {

        Reservation reservation = confirmUseCase.confirm(reservationId);
        return ApiResponse.success(ReservationResponse.from(reservation));
    }

    @PatchMapping("/{reservationId}/cancel")
    public ApiResponse<Void> cancel(
            @PathVariable Long reservationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestBody CancelReservationRequest request) {

        cancelUseCase.cancel(reservationId, partnerId, request.reason());
        return ApiResponse.success(null);
    }

    @GetMapping("/accommodations/{accommodationId}")
    public ApiResponse<List<ReservationResponse>> getByAccommodation(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        List<ReservationResponse> responses = getQuery.getByAccommodationId(accommodationId, partnerId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    public record CancelReservationRequest(String reason) {
    }
}
