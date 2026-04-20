package com.accommodation.platform.core.reservation.application.port.out;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface PersistReservationPort {

    Reservation save(Reservation reservation);
}
