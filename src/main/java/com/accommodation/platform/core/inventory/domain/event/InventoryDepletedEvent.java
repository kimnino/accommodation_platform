package com.accommodation.platform.core.inventory.domain.event;

import java.time.Instant;
import java.time.LocalDate;

public record InventoryDepletedEvent(Long inventoryId, Long roomOptionId, LocalDate date, Instant occurredAt) {

    public static InventoryDepletedEvent of(Long inventoryId, Long roomOptionId, LocalDate date) {

        return new InventoryDepletedEvent(inventoryId, roomOptionId, date, Instant.now());
    }
}
