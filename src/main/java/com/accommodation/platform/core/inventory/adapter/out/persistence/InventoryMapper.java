package com.accommodation.platform.core.inventory.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.inventory.domain.model.Inventory;

@Component
public class InventoryMapper {

    public Inventory toDomain(InventoryJpaEntity entity) {

        Inventory inventory = Inventory.builder()
                .id(entity.getId())
                .roomOptionId(entity.getRoomOptionId())
                .date(entity.getDate())
                .totalQuantity(entity.getTotalQuantity())
                .remainingQuantity(entity.getRemainingQuantity())
                .build();

        inventory.setCreatedAt(entity.getCreatedAt());
        inventory.setUpdatedAt(entity.getUpdatedAt());

        return inventory;
    }

    public InventoryJpaEntity toJpaEntity(Inventory domain) {

        return new InventoryJpaEntity(
                domain.getId(),
                domain.getRoomOptionId(),
                domain.getDate(),
                domain.getTotalQuantity(),
                domain.getRemainingQuantity(),
                domain.getStatus());
    }
}
