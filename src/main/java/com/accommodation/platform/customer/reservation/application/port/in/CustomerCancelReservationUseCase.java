package com.accommodation.platform.customer.reservation.application.port.in;

public interface CustomerCancelReservationUseCase {

    void cancel(Long reservationId, Long memberId);
}
