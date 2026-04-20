package com.accommodation.platform.customer.reservation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface CustomerGetReservationQuery {

    Reservation getById(Long reservationId, Long memberId);

    List<Reservation> getByMemberId(Long memberId);
}
