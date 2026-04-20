package com.accommodation.platform.extranet.inventory.adapter.in.web;

import java.time.LocalDate;

import com.accommodation.platform.core.inventory.domain.model.Inventory;

public record InventoryResponse(
        Long id,
        Long roomOptionId,
        LocalDate date,
        int totalQuantity,
        int remainingQuantity,
        String status
) {

    public static InventoryResponse from(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getRoomOptionId(),
                inventory.getDate(),
                inventory.getTotalQuantity(),
                inventory.getRemainingQuantity(),
                inventory.getStatus().name());
    }
}
