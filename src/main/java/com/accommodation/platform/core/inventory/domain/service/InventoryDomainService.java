package com.accommodation.platform.core.inventory.domain.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

public class InventoryDomainService {

    private static final int SLOT_UNIT_MINUTES = 30;

    /**
     * 연박 가용성 판단.
     * checkIn ~ checkOut-1 날짜 범위의 모든 재고가 available이어야 연박 가능.
     */
    public boolean isConsecutiveAvailable(List<Inventory> inventories,
                                          LocalDate checkIn, LocalDate checkOut) {

        long nights = checkIn.until(checkOut).getDays();
        if (nights <= 0) {
            return false;
        }

        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            LocalDate targetDate = date;
            boolean available = inventories.stream()
                    .filter(inv -> inv.getDate().equals(targetDate))
                    .anyMatch(Inventory::isAvailable);
            if (!available) {
                return false;
            }
        }
        return true;
    }

    /**
     * 대실 예약 시 연속 블록 점유 + 버퍼 블록 차단.
     * startTime부터 usageDuration만큼 OCCUPIED, 이후 buffer만큼 BLOCKED.
     */
    public void occupySlotsForHourlyBooking(List<TimeSlotInventory> daySlots,
                                             LocalTime startTime, int usageDurationMinutes,
                                             int bufferMinutes) {

        LocalTime usageEnd = startTime.plusMinutes(usageDurationMinutes);
        LocalTime bufferEnd = usageEnd.plusMinutes(bufferMinutes);

        for (TimeSlotInventory slot : daySlots) {
            LocalTime slotTime = slot.getSlotTime();

            if (!slotTime.isBefore(startTime) && slotTime.isBefore(usageEnd)) {
                slot.occupy();
            } else if (!slotTime.isBefore(usageEnd) && slotTime.isBefore(bufferEnd)) {
                slot.block();
            }
        }
    }

    /**
     * 특정 시작시간부터 실제 이용 가능한 시간(분)을 계산.
     * - 연속 AVAILABLE 블록 수 × 30분
     * - 운영 종료시간까지로 제한
     * - 정규 이용시간(usageDuration) 미만이면 단축 이용 가능 시간을 반환
     *
     * @return 이용 가능 시간(분). 0이면 예약 불가.
     */
    public int calculateAvailableMinutes(List<TimeSlotInventory> daySlots,
                                          LocalTime startTime, LocalTime operatingEndTime) {

        int availableMinutes = 0;
        LocalTime current = startTime;

        while (current.isBefore(operatingEndTime)) {
            LocalTime checkTime = current;
            boolean slotAvailable = daySlots.stream()
                    .filter(s -> s.getSlotTime().equals(checkTime))
                    .anyMatch(TimeSlotInventory::isAvailable);

            if (!slotAvailable) {
                break;
            }
            availableMinutes += SLOT_UNIT_MINUTES;
            current = current.plusMinutes(SLOT_UNIT_MINUTES);
        }

        return availableMinutes;
    }

    /**
     * 대실 예약 가능 여부 확인.
     * - 정규 이용시간(usageDuration) 이상 연속 블록이 있으면 정상 예약
     * - 운영 종료 임박으로 정규 시간 미만이면 단축 이용 가능 (고객 동의 필요)
     * - 연속 블록이 0이면 예약 불가
     *
     * @return 실제 이용 가능 시간(분). 0이면 예약 불가.
     */
    public int getBookableMinutes(List<TimeSlotInventory> daySlots,
                                   LocalTime startTime, int usageDurationMinutes,
                                   LocalTime operatingEndTime) {

        int availableMinutes = calculateAvailableMinutes(daySlots, startTime, operatingEndTime);

        if (availableMinutes >= usageDurationMinutes) {
            return usageDurationMinutes;
        }

        return availableMinutes;
    }

    /**
     * 숙박 예약 시 숙박 시간대와 겹치는 대실 블록만 점유.
     * 숙박 체크인~다음날 체크아웃 시간과 겹치지 않는 대실 슬롯은 유지.
     * 예: 체크인 21:00, 대실 12:00~20:00 → 겹치지 않으므로 대실 슬롯 유지.
     */
    public void occupySlotsForStayBooking(List<TimeSlotInventory> daySlots,
                                           LocalTime stayCheckInTime) {

        for (TimeSlotInventory slot : daySlots) {
            if (slot.isAvailable() && !slot.getSlotTime().isBefore(stayCheckInTime)) {
                slot.occupy();
            }
        }
    }

    /**
     * 대실 블록 사용 현황 기반으로 숙박 가능 여부 판단.
     * 숙박 체크인 시간 이후의 슬롯 중 OCCUPIED/BLOCKED가 있으면 숙박 불가.
     * 대실 운영 시간(예: 12:00~20:00)과 숙박 체크인(예: 21:00)이 겹치지 않으면 공존 가능.
     */
    public boolean canAcceptStayAfterHourlyBookings(List<TimeSlotInventory> daySlots,
                                                     LocalTime stayCheckInTime) {

        return daySlots.stream()
                .filter(slot -> !slot.getSlotTime().isBefore(stayCheckInTime))
                .allMatch(TimeSlotInventory::isAvailable);
    }
}
