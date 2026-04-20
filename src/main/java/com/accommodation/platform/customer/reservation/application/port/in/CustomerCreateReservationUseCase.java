package com.accommodation.platform.customer.reservation.application.port.in;

import java.time.LocalDate;
import java.time.LocalTime;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface CustomerCreateReservationUseCase {

    Reservation createStayReservation(CreateStayReservationCommand command);

    Reservation createHourlyReservation(CreateHourlyReservationCommand command);

    record CreateStayReservationCommand(
            String reservationRequestId,
            Long memberId,
            Long accommodationId,
            Long roomOptionId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            String guestName,
            String guestPhone,
            String guestEmail
    ) {
    }

    record CreateHourlyReservationCommand(
            String reservationRequestId,
            Long memberId,
            Long accommodationId,
            Long roomOptionId,
            LocalDate date,
            LocalTime startTime,
            String guestName,
            String guestPhone,
            String guestEmail
    ) {
    }
}
