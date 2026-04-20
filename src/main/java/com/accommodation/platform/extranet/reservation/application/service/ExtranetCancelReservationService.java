package com.accommodation.platform.extranet.reservation.application.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.extranet.reservation.application.port.in.ExtranetCancelReservationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetCancelReservationService implements ExtranetCancelReservationUseCase {

    private final LoadReservationPort loadReservationPort;
    private final PersistReservationPort persistReservationPort;
    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadInventoryPort loadInventoryPort;
    private final PersistInventoryPort persistInventoryPort;

    @Override
    public void cancel(Long reservationId, Long partnerId, String reason) {

        Reservation reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        loadAccommodationPort.findById(reservation.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        reservation.cancel();

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
        log.info("파트너 예약 취소: {} (사유: {})", reservation.getReservationNumber(), reason);
    }
}
