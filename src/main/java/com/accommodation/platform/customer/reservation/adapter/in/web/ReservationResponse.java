package com.accommodation.platform.customer.reservation.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public record ReservationResponse(
        Long id,
        String reservationNumber,
        String reservationType,
        Long accommodationId,
        Long roomOptionId,
        Long memberId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        LocalTime hourlyStartTime,
        int hourlyUsageMinutes,
        String guestName,
        String guestPhone,
        String guestEmail,
        BigDecimal totalPrice,
        String status,
        Instant holdExpiredAt,
        Instant createdAt,
        Instant updatedAt
) {

    public static ReservationResponse from(Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getReservationType().name(),
                reservation.getAccommodationId(),
                reservation.getRoomOptionId(),
                reservation.getMemberId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getHourlyStartTime(),
                reservation.getHourlyUsageMinutes(),
                reservation.getGuestInfo().name(),
                reservation.getGuestInfo().phone(),
                reservation.getGuestInfo().email(),
                reservation.getTotalPrice(),
                reservation.getStatus().name(),
                reservation.getHoldExpiredAt(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt());
    }
}
