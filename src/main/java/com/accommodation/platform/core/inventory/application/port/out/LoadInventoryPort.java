package com.accommodation.platform.core.inventory.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.inventory.domain.model.Inventory;

public interface LoadInventoryPort {

    Optional<Inventory> findById(Long id);

    List<Inventory> findByRoomOptionIdAndDateRange(Long roomOptionId, LocalDate startDate, LocalDate endDate);

    /**
     * 비관적 락으로 재고 조회 (동시성 제어).
     * 예약 시 SELECT ... FOR UPDATE로 잠금.
     */
    Optional<Inventory> findWithLock(Long roomOptionId, LocalDate date);
}
