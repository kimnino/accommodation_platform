package com.accommodation.platform.core.reservation.domain.event;

import java.time.Instant;

public record ReservationConfirmedEvent(Long reservationId, String reservationNumber, Instant occurredAt) {

    public static ReservationConfirmedEvent of(Long reservationId, String reservationNumber) {

        return new ReservationConfirmedEvent(reservationId, reservationNumber, Instant.now());
    }
}
