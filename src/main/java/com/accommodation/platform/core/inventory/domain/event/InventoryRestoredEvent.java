package com.accommodation.platform.core.inventory.domain.event;

import java.time.Instant;
import java.time.LocalDate;

public record InventoryRestoredEvent(
        Long inventoryId,
        Long roomOptionId,
        LocalDate date,
        int restoredQuantity,
        Instant occurredAt
) {

    public static InventoryRestoredEvent of(Long inventoryId, Long roomOptionId,
                                             LocalDate date, int restoredQuantity) {

        return new InventoryRestoredEvent(inventoryId, roomOptionId, date, restoredQuantity, Instant.now());
    }
}
