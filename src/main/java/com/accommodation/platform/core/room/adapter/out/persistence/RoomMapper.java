package com.accommodation.platform.core.room.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RoomMapper {

    default Room toDomain(RoomJpaEntity entity) {
        Room room = Room.reconstruct(
                entity.getId(),
                entity.getAccommodationId(),
                entity.getName(),
                entity.getRoomTypeName(),
                entity.getStandardCapacity(),
                entity.getMaxCapacity(),
                entity.getStatus());
        room.setCreatedAt(entity.getCreatedAt());
        room.setUpdatedAt(entity.getUpdatedAt());
        return room;
    }

    RoomJpaEntity toJpaEntity(Room domain);

    RoomOption toDomain(RoomOptionJpaEntity entity);

    RoomOptionJpaEntity toJpaEntity(RoomOption domain);

    @AfterMapping
    default void restoreRoomJpaTimestamps(@MappingTarget RoomJpaEntity entity, Room domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }

    @AfterMapping
    default void restoreRoomOptionJpaTimestamps(@MappingTarget RoomOptionJpaEntity entity, RoomOption domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }
}
