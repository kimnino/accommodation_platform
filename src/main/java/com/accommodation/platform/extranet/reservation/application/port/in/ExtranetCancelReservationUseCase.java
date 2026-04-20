package com.accommodation.platform.extranet.reservation.application.port.in;

public interface ExtranetCancelReservationUseCase {

    void cancel(Long reservationId, Long partnerId, String reason);
}
