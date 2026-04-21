package com.accommodation.platform.core.inventory.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

public interface LoadInventoryPort {

    Optional<Inventory> findById(Long id);

    List<Inventory> findByRoomOptionIdAndDateRange(Long roomOptionId, LocalDate startDate, LocalDate endDate);

    /**
     * 비관적 락으로 재고 조회 (동시성 제어).
     * 예약 시 SELECT ... FOR UPDATE로 잠금.
     */
    Optional<Inventory> findWithLock(Long roomOptionId, LocalDate date);

    /**
     * 대실 시간 슬롯 범위 조회 — 비관적 락.
     * startTime 이상 endTime 미만 슬롯을 SELECT FOR UPDATE.
     */
    List<TimeSlotInventory> findTimeSlotsWithLock(Long roomOptionId, LocalDate date,
                                                   LocalTime startTime, LocalTime endTime);

    /**
     * 버퍼 슬롯 조회 (청소 시간 차단용) — 단일 슬롯.
     */
    Optional<TimeSlotInventory> findTimeSlot(Long roomOptionId, LocalDate date, LocalTime slotTime);
}
