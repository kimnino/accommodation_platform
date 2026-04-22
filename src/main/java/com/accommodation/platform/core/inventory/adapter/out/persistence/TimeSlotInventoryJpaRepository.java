package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface TimeSlotInventoryJpaRepository extends JpaRepository<TimeSlotInventoryJpaEntity, Long> {

    List<TimeSlotInventoryJpaEntity> findByRoomOptionIdAndDateOrderBySlotTimeAsc(
            Long roomOptionId, LocalDate date);

    List<TimeSlotInventoryJpaEntity> findByRoomOptionIdAndDateAndSlotTimeBetweenOrderBySlotTimeAsc(
            Long roomOptionId, LocalDate date, LocalTime startSlot, LocalTime endSlot);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ts FROM TimeSlotInventoryJpaEntity ts " +
            "WHERE ts.roomOptionId = :roomOptionId AND ts.date = :date " +
            "AND ts.slotTime >= :startTime AND ts.slotTime < :endTime " +
            "ORDER BY ts.slotTime ASC")
    List<TimeSlotInventoryJpaEntity> findWithLockByRange(
            Long roomOptionId, LocalDate date, LocalTime startTime, LocalTime endTime);

    Optional<TimeSlotInventoryJpaEntity> findByRoomOptionIdAndDateAndSlotTime(
            Long roomOptionId, LocalDate date, LocalTime slotTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ts FROM TimeSlotInventoryJpaEntity ts " +
            "WHERE ts.roomOptionId = :roomOptionId AND ts.date = :date AND ts.status = 'AVAILABLE' " +
            "ORDER BY ts.slotTime ASC")
    List<TimeSlotInventoryJpaEntity> findAvailableWithLock(Long roomOptionId, LocalDate date);
}
