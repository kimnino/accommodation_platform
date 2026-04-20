package com.accommodation.platform.extranet.reservation.application.port.in;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface ExtranetConfirmReservationUseCase {

    Reservation confirm(Long reservationId);
}
