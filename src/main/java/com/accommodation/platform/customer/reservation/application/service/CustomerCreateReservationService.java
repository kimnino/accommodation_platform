package com.accommodation.platform.customer.reservation.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadHourlySettingPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationHourlySetting;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;
import com.accommodation.platform.core.inventory.domain.service.InventoryDomainService;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.core.price.domain.service.PriceDomainService;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;
import com.accommodation.platform.core.reservation.domain.model.GuestInfo;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerCreateReservationService implements CustomerCreateReservationUseCase {

    private static final int HOLD_MINUTES = 10;

    private final PersistReservationPort persistReservationPort;
    private final LoadReservationPort loadReservationPort;
    private final LoadInventoryPort loadInventoryPort;
    private final PersistInventoryPort persistInventoryPort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final PriceDomainService priceDomainService;
    private final InventoryDomainService inventoryDomainService;
    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadHourlySettingPort loadHourlySettingPort;

    @Override
    public Reservation createStayReservation(CreateStayReservationCommand command) {

        loadReservationPort.findByReservationRequestId(command.reservationRequestId())
                .ifPresent(existing -> {
                    throw new BusinessException(ErrorCode.INVALID_INPUT, "이미 처리된 예약 요청입니다.");
                });

        Accommodation accommodation = loadAccommodationPort.findById(command.accommodationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));
        LocalTime checkInTime = accommodation.getCheckInTime();

        // STAY↔HOURLY 충돌 사전 확인 (락 없음)
        for (LocalDate date = command.checkInDate(); date.isBefore(command.checkOutDate()); date = date.plusDays(1)) {
            List<TimeSlotInventory> slots = loadInventoryPort.findTimeSlotsByDate(command.roomOptionId(), date);
            if (!slots.isEmpty() && !inventoryDomainService.canAcceptStayAfterHourlyBookings(slots, checkInTime)) {
                throw new BusinessException(ErrorCode.INVENTORY_NOT_AVAILABLE,
                        "해당 날짜(" + date + ")에 대실 예약이 있어 숙박 예약이 불가합니다.");
            }
        }

        // 날짜별 재고 비관적 락 + 차감
        for (LocalDate date = command.checkInDate(); date.isBefore(command.checkOutDate()); date = date.plusDays(1)) {
            Inventory inventory = loadInventoryPort.findWithLock(command.roomOptionId(), date)
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_AVAILABLE));

            if (!inventory.isAvailable()) {
                throw new BusinessException(ErrorCode.INVENTORY_NOT_AVAILABLE);
            }

            inventory.decrease(1);
            persistInventoryPort.save(inventory);

            // 숙박 재고 소진 시 대실 슬롯 차단 (STAY↔HOURLY 충돌 방지)
            if (!inventory.isAvailable()) {
                List<TimeSlotInventory> availableSlots = loadInventoryPort.findAvailableTimeSlotsWithLock(
                        command.roomOptionId(), date);
                if (!availableSlots.isEmpty()) {
                    inventoryDomainService.occupySlotsForStayBooking(availableSlots, checkInTime);
                    persistInventoryPort.saveAllTimeSlots(availableSlots);
                    log.info("숙박 재고 소진으로 대실 슬롯 차단: roomOptionId={}, date={}", command.roomOptionId(), date);
                }
            }
        }

        List<RoomPrice> prices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                command.roomOptionId(), PriceType.STAY,
                command.checkInDate(), command.checkOutDate().minusDays(1));
        BigDecimal totalPrice = priceDomainService.calculateTotalPriceWithVat(prices);

        Reservation reservation = Reservation.builder()
                .reservationRequestId(command.reservationRequestId())
                .memberId(command.memberId())
                .accommodationId(command.accommodationId())
                .roomOptionId(command.roomOptionId())
                .reservationType(ReservationType.STAY)
                .checkInDate(command.checkInDate())
                .checkOutDate(command.checkOutDate())
                .guestInfo(new GuestInfo(command.guestName(), command.guestPhone(), command.guestEmail()))
                .totalPrice(totalPrice)
                .build();

        reservation.holdForPayment(Instant.now().plus(HOLD_MINUTES, ChronoUnit.MINUTES));

        Reservation saved = persistReservationPort.save(reservation);
        log.info("예약 생성: {} (PAYMENT_WAITING, {}분 내 결제 필요)", saved.getReservationNumber(), HOLD_MINUTES);

        return saved;
    }

    @Override
    public Reservation createHourlyReservation(CreateHourlyReservationCommand command) {

        loadReservationPort.findByReservationRequestId(command.reservationRequestId())
                .ifPresent(existing -> {
                    throw new BusinessException(ErrorCode.INVALID_INPUT, "이미 처리된 예약 요청입니다.");
                });

        // 시간 슬롯 비관적 락 조회 (startTime 이상 endTime 미만)
        List<TimeSlotInventory> slots = loadInventoryPort.findTimeSlotsWithLock(
                command.roomOptionId(), command.date(), command.startTime(), command.endTime());

        if (slots.isEmpty()) {
            throw new BusinessException(ErrorCode.INVENTORY_NOT_AVAILABLE, "해당 시간대에 예약 가능한 슬롯이 없습니다.");
        }

        int slotUnitMinutes = loadHourlySettingPort.findByAccommodationId(command.accommodationId())
                .map(AccommodationHourlySetting::getSlotUnitMinutes)
                .orElse(30);
        long requiredSlots = ChronoUnit.MINUTES.between(command.startTime(), command.endTime()) / slotUnitMinutes;
        if (slots.size() < requiredSlots) {
            throw new BusinessException(ErrorCode.INVENTORY_NOT_AVAILABLE, "요청한 시간 범위의 슬롯을 찾을 수 없습니다.");
        }

        slots.forEach(slot -> {
            if (!slot.isAvailable()) {
                throw new BusinessException(ErrorCode.INVENTORY_NOT_AVAILABLE,
                        slot.getSlotTime() + " 슬롯이 이미 예약되어 있습니다.");
            }
        });

        int usageDurationMinutes = (int) ChronoUnit.MINUTES.between(command.startTime(), command.endTime());
        inventoryDomainService.occupySlotsForHourlyBooking(slots, command.startTime(), usageDurationMinutes, 0);

        // 버퍼 슬롯 차단 (청소 시간 — endTime 슬롯 1개)
        List<TimeSlotInventory> toSave = new ArrayList<>(slots);
        loadInventoryPort.findTimeSlot(command.roomOptionId(), command.date(), command.endTime())
                .ifPresent(buffer -> {
                    if (buffer.isAvailable()) {
                        buffer.block();
                        toSave.add(buffer);
                    }
                });

        persistInventoryPort.saveAllTimeSlots(toSave);

        List<RoomPrice> prices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                command.roomOptionId(), PriceType.HOURLY, command.date(), command.date());
        BigDecimal totalPrice = prices.isEmpty() ? BigDecimal.ZERO : prices.getFirst().getSellingPrice();

        Reservation reservation = Reservation.builder()
                .reservationRequestId(command.reservationRequestId())
                .memberId(command.memberId())
                .accommodationId(command.accommodationId())
                .roomOptionId(command.roomOptionId())
                .reservationType(ReservationType.HOURLY)
                .checkInDate(command.date())
                .checkOutDate(command.date())
                .hourlyStartTime(command.startTime())
                .hourlyUsageMinutes(usageDurationMinutes)
                .guestInfo(new GuestInfo(command.guestName(), command.guestPhone(), command.guestEmail()))
                .totalPrice(totalPrice)
                .build();

        reservation.holdForPayment(Instant.now().plus(HOLD_MINUTES, ChronoUnit.MINUTES));

        Reservation saved = persistReservationPort.save(reservation);
        log.info("대실 예약 생성: {} {}~{} (PAYMENT_WAITING)", saved.getReservationNumber(),
                command.startTime(), command.endTime());

        return saved;
    }
}
