package com.accommodation.platform.extranet.reservation.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.extranet.reservation.application.port.in.ExtranetConfirmReservationUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetConfirmReservationService implements ExtranetConfirmReservationUseCase {

    private final LoadReservationPort loadReservationPort;
    private final PersistReservationPort persistReservationPort;

    @Override
    public Reservation confirm(Long reservationId) {

        Reservation reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.confirm();
        return persistReservationPort.save(reservation);
    }
}
