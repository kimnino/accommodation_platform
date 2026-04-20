package com.accommodation.platform.customer.reservation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerGetReservationQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerGetReservationService implements CustomerGetReservationQuery {

    private final LoadReservationPort loadReservationPort;

    @Override
    public Reservation getById(Long reservationId, Long memberId) {

        Reservation reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, "해당 예약에 대한 접근 권한이 없습니다.");
        }

        return reservation;
    }

    @Override
    public List<Reservation> getByMemberId(Long memberId) {

        return loadReservationPort.findByMemberId(memberId);
    }
}
