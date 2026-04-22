package com.accommodation.platform.customer.reservation.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.common.response.ReservationResponse;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCancelReservationUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerConfirmPaymentUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerGetReservationQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class CustomerReservationController {

    private final CustomerCreateReservationUseCase createUseCase;
    private final CustomerCancelReservationUseCase cancelUseCase;
    private final CustomerConfirmPaymentUseCase confirmPaymentUseCase;
    private final CustomerGetReservationQuery getQuery;

    @PostMapping("/stay")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> createStayReservation(
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CreateStayReservationRequest request) {

        Reservation reservation = createUseCase.createStayReservation(request.toCommand(memberId));
        return ApiResponse.success(ReservationResponse.from(reservation));
    }

    @PostMapping("/hourly")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> createHourlyReservation(
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CreateHourlyReservationRequest request) {

        Reservation reservation = createUseCase.createHourlyReservation(request.toCommand(memberId));
        return ApiResponse.success(ReservationResponse.from(reservation));
    }

    @PostMapping("/{reservationId}/confirm-payment")
    public ApiResponse<ReservationResponse> confirmPayment(
            @PathVariable Long reservationId,
            @RequestHeader("X-Member-Id") Long memberId) {

        Reservation reservation = confirmPaymentUseCase.confirmPayment(reservationId, memberId);
        return ApiResponse.success(ReservationResponse.from(reservation));
    }

    @DeleteMapping("/{reservationId}")
    public ApiResponse<Void> cancel(
            @PathVariable Long reservationId,
            @RequestHeader("X-Member-Id") Long memberId) {

        cancelUseCase.cancel(reservationId, memberId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{reservationId}")
    public ApiResponse<ReservationResponse> getById(
            @PathVariable Long reservationId,
            @RequestHeader("X-Member-Id") Long memberId) {

        Reservation reservation = getQuery.getById(reservationId, memberId);
        return ApiResponse.success(ReservationResponse.from(reservation));
    }

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getMyReservations(
            @RequestHeader("X-Member-Id") Long memberId) {

        List<ReservationResponse> responses = getQuery.getByMemberId(memberId).stream()
                .map(ReservationResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
