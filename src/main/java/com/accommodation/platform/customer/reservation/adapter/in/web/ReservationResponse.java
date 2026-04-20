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
        LocalDate checkInDate,
        LocalDate checkOutDate,
        LocalTime hourlyStartTime,
        String guestName,
        BigDecimal totalPrice,
        String status,
        Instant holdExpiredAt,
        Instant createdAt
) {

    public static ReservationResponse from(Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getReservationType().name(),
                reservation.getAccommodationId(),
                reservation.getRoomOptionId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getHourlyStartTime(),
                reservation.getGuestInfo().name(),
                reservation.getTotalPrice(),
                reservation.getStatus().name(),
                reservation.getHoldExpiredAt(),
                reservation.getCreatedAt());
    }
}
