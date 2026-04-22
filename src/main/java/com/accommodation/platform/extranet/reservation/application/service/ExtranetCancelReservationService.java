package com.accommodation.platform.extranet.reservation.application.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadHourlySettingPort;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationHourlySetting;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.extranet.common.ExtranetOwnershipVerifier;
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
    private final ExtranetOwnershipVerifier ownershipVerifier;
    private final LoadInventoryPort loadInventoryPort;
    private final PersistInventoryPort persistInventoryPort;
    private final LoadHourlySettingPort loadHourlySettingPort;

    @Override
    public void cancel(Long reservationId, Long partnerId, String reason) {

        Reservation reservation = loadReservationPort.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        ownershipVerifier.verifyAccommodationOwnership(reservation.getAccommodationId(), partnerId);

        reservation.cancel();

        if (reservation.getReservationType() == ReservationType.STAY) {
            restoreStayInventory(reservation);
        } else if (reservation.getReservationType() == ReservationType.HOURLY) {
            restoreHourlyInventory(reservation);
        }

        persistReservationPort.save(reservation);
        log.info("파트너 예약 취소: {} (사유: {})", reservation.getReservationNumber(), reason);
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

    private void restoreHourlyInventory(Reservation reservation) {

        int slotUnitMinutes = loadHourlySettingPort.findByAccommodationId(reservation.getAccommodationId())
                .map(AccommodationHourlySetting::getSlotUnitMinutes)
                .orElse(30);

        LocalTime startTime = reservation.getHourlyStartTime();
        LocalTime bufferEnd = startTime.plusMinutes(reservation.getHourlyUsageMinutes() + (long) slotUnitMinutes);

        List<TimeSlotInventory> slots = loadInventoryPort.findTimeSlotsWithLock(
                reservation.getRoomOptionId(), reservation.getCheckInDate(), startTime, bufferEnd);

        List<TimeSlotInventory> toRelease = slots.stream()
                .filter(s -> !s.isAvailable())
                .toList();

        toRelease.forEach(TimeSlotInventory::release);
        if (!toRelease.isEmpty()) {
            persistInventoryPort.saveAllTimeSlots(toRelease);
        }
    }
}
