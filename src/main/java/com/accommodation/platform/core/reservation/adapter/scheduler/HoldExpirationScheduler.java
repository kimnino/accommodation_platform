package com.accommodation.platform.core.reservation.adapter.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;
import com.accommodation.platform.core.reservation.domain.model.Reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldExpirationScheduler {

    private final LoadReservationPort loadReservationPort;
    private final PersistReservationPort persistReservationPort;
    private final LoadInventoryPort loadInventoryPort;
    private final PersistInventoryPort persistInventoryPort;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cancelExpiredHolds() {

        List<Reservation> waitingReservations = loadReservationPort.findByStatus(ReservationStatus.PAYMENT_WAITING);

        for (Reservation reservation : waitingReservations) {
            if (reservation.isHoldExpired()) {
                reservation.cancel();

                if (reservation.getReservationType() == ReservationType.STAY) {
                    restoreStayInventory(reservation);
                }

                persistReservationPort.save(reservation);
                log.info("Hold 만료 자동 취소: {} → 재고 복구", reservation.getReservationNumber());
            }
        }
    }

    private void restoreStayInventory(Reservation reservation) {

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
}
