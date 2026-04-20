package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotInventoryJpaRepository extends JpaRepository<TimeSlotInventoryJpaEntity, Long> {

    List<TimeSlotInventoryJpaEntity> findByRoomOptionIdAndDateOrderBySlotTimeAsc(
            Long roomOptionId, LocalDate date);

    List<TimeSlotInventoryJpaEntity> findByRoomOptionIdAndDateAndSlotTimeBetweenOrderBySlotTimeAsc(
            Long roomOptionId, LocalDate date, LocalTime startSlot, LocalTime endSlot);
}
