package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InventoryJpaAdapter implements PersistInventoryPort, LoadInventoryPort {

    private final InventoryJpaRepository jpaRepository;
    private final TimeSlotInventoryJpaRepository timeSlotJpaRepository;
    private final InventoryMapper mapper;

    @Override
    public Inventory save(Inventory inventory) {

        InventoryJpaEntity entity = mapper.toJpaEntity(inventory);
        InventoryJpaEntity saved = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Inventory> saveAll(List<Inventory> inventories) {

        List<InventoryJpaEntity> entities = inventories.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<InventoryJpaEntity> savedEntities = jpaRepository.saveAllAndFlush(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Inventory> findById(Long id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Inventory> findByRoomOptionIdAndDateRange(Long roomOptionId,
                                                          LocalDate startDate, LocalDate endDate) {

        return jpaRepository.findByRoomOptionIdAndDateBetweenOrderByDateAsc(roomOptionId, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Inventory> findWithLock(Long roomOptionId, LocalDate date) {

        return jpaRepository.findWithLock(roomOptionId, date)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByRoomOptionId(Long roomOptionId) {

        jpaRepository.deleteByRoomOptionId(roomOptionId);
    }

    @Override
    public List<TimeSlotInventory> findTimeSlotsWithLock(Long roomOptionId, LocalDate date,
                                                          LocalTime startTime, LocalTime endTime) {

        return timeSlotJpaRepository.findWithLockByRange(roomOptionId, date, startTime, endTime)
                .stream()
                .map(mapper::toTimeSlotDomain)
                .toList();
    }

    @Override
    public Optional<TimeSlotInventory> findTimeSlot(Long roomOptionId, LocalDate date, LocalTime slotTime) {

        return timeSlotJpaRepository.findByRoomOptionIdAndDateAndSlotTime(roomOptionId, date, slotTime)
                .map(mapper::toTimeSlotDomain);
    }

    @Override
    public List<TimeSlotInventory> findAvailableTimeSlotsWithLock(Long roomOptionId, LocalDate date) {

        return timeSlotJpaRepository.findAvailableWithLock(roomOptionId, date)
                .stream()
                .map(mapper::toTimeSlotDomain)
                .toList();
    }

    @Override
    public List<TimeSlotInventory> findTimeSlotsByDate(Long roomOptionId, LocalDate date) {

        return timeSlotJpaRepository.findByRoomOptionIdAndDateOrderBySlotTimeAsc(roomOptionId, date)
                .stream()
                .map(mapper::toTimeSlotDomain)
                .toList();
    }

    @Override
    public List<TimeSlotInventory> saveAllTimeSlots(List<TimeSlotInventory> timeSlots) {

        List<TimeSlotInventoryJpaEntity> entities = timeSlots.stream()
                .map(mapper::toTimeSlotJpaEntity)
                .toList();
        return timeSlotJpaRepository.saveAllAndFlush(entities)
                .stream()
                .map(mapper::toTimeSlotDomain)
                .toList();
    }
}
