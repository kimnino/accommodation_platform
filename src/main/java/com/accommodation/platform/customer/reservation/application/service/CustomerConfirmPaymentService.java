package com.accommodation.platform.customer.reservation.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerConfirmPaymentUseCase;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerConfirmPaymentService implements CustomerConfirmPaymentUseCase {

    private final LoadReservationPort loadReservationPort;
    private final PersistReservationPort persistReservationPort;

    @Override
    public Reservation confirmPayment(Long reservationId, Long memberId) {

        Reservation reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "예약을 찾을 수 없습니다."));

        if (!reservation.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인 예약만 결제 확정할 수 있습니다.");
        }

        reservation.confirm();
        Reservation saved = persistReservationPort.save(reservation);
        log.info("결제 확정: {} → CONFIRMED", saved.getReservationNumber());
        return saved;
    }
}
