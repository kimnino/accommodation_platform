package com.accommodation.platform.core.reservation.domain.event;

import java.time.Instant;

public record ReservationCancelledEvent(Long reservationId, Long roomOptionId, Instant occurredAt) {

    public static ReservationCancelledEvent of(Long reservationId, Long roomOptionId) {

        return new ReservationCancelledEvent(reservationId, roomOptionId, Instant.now());
    }
}
