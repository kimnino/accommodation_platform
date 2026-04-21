package com.accommodation.platform.customer.reservation.application.port.in;

import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface CustomerConfirmPaymentUseCase {

    Reservation confirmPayment(Long reservationId, Long memberId);
}
