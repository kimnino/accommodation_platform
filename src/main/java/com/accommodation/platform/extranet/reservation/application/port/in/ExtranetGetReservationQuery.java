package com.accommodation.platform.extranet.reservation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface ExtranetGetReservationQuery {

    List<Reservation> getByAccommodationId(Long accommodationId, Long partnerId);
}
