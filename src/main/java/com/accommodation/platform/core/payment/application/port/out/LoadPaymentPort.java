package com.accommodation.platform.core.payment.application.port.out;

import java.util.Optional;

import com.accommodation.platform.core.payment.domain.model.Payment;

public interface LoadPaymentPort {

    /**
     * ID로 결제를 조회한다.
     *
     * @param id 결제 ID
     * @return 결제 (없으면 empty)
     */
    Optional<Payment> findById(Long id);

    /**
     * 예약 ID로 결제를 조회한다.
     *
     * @param reservationId 예약 ID
     * @return 결제 (없으면 empty)
     */
    Optional<Payment> findByReservationId(Long reservationId);
}
