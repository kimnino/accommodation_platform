package com.accommodation.platform.customer.reservation.application.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCancelReservationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerCancelReservationService implements CustomerCancelReservationUseCase {

    private final LoadReservationPort loadReservationPort;
    private final PersistReservationPort persistReservationPort;
    private final LoadInventoryPort loadInventoryPort;
    private final PersistInventoryPort persistInventoryPort;

    @Override
    public void cancel(Long reservationId, Long memberId) {

        Reservation reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, "해당 예약에 대한 접근 권한이 없습니다.");
        }

        reservation.cancel();

        // 숙박 재고 복구
        if (reservation.getReservationType() == ReservationType.STAY) {
            for (LocalDate date = reservation.getCheckInDate();
                 date.isBefore(reservation.getCheckOutDate());
                 date = date.plusDays(1)) {

                loadInventoryPort.findWithLock(reservation.getRoomOptionId(), date)
                        .ifPresent(inventory -> {
                            inventory.increase(1);
                            persistInventoryPort.save(inventory);
                        });
            }
        }

        persistReservationPort.save(reservation);
        log.info("예약 취소: {} → 재고 복구 완료", reservation.getReservationNumber());
    }
}
