package com.accommodation.platform.core.inventory.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface InventoryMapper {

    Inventory toDomain(InventoryJpaEntity entity);

    InventoryJpaEntity toJpaEntity(Inventory domain);

    TimeSlotInventory toTimeSlotDomain(TimeSlotInventoryJpaEntity entity);

    TimeSlotInventoryJpaEntity toTimeSlotJpaEntity(TimeSlotInventory domain);

    @AfterMapping
    default void restoreInventoryJpaTimestamps(@MappingTarget InventoryJpaEntity entity, Inventory domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }

    @AfterMapping
    default void restoreTimeSlotJpaTimestamps(@MappingTarget TimeSlotInventoryJpaEntity entity, TimeSlotInventory domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }
}
