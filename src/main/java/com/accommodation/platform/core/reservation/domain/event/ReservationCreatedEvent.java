package com.accommodation.platform.core.reservation.domain.event;

import java.time.Instant;

public record ReservationCreatedEvent(Long reservationId, String reservationNumber, Long roomOptionId,
                                       Instant occurredAt) {

    public static ReservationCreatedEvent of(Long reservationId, String reservationNumber, Long roomOptionId) {

        return new ReservationCreatedEvent(reservationId, reservationNumber, roomOptionId, Instant.now());
    }
}
