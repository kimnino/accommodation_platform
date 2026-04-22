package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, Long> {

    List<InventoryJpaEntity> findByRoomOptionIdAndDateBetweenOrderByDateAsc(
            Long roomOptionId, LocalDate startDate, LocalDate endDate);

    /** 비관적 락으로 재고 조회 — 예약 동시성 제어용 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryJpaEntity i WHERE i.roomOptionId = :roomOptionId AND i.date = :date")
    Optional<InventoryJpaEntity> findWithLock(Long roomOptionId, LocalDate date);

    List<InventoryJpaEntity> findByRoomOptionIdInAndDateBetweenOrderByRoomOptionIdAscDateAsc(
            List<Long> roomOptionIds, LocalDate startDate, LocalDate endDate);

    void deleteByRoomOptionId(Long roomOptionId);
}
