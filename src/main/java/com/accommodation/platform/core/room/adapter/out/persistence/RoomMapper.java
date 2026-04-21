package com.accommodation.platform.core.room.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RoomMapper {

    Room toDomain(RoomJpaEntity entity);

    RoomJpaEntity toJpaEntity(Room domain);

    RoomOption toDomain(RoomOptionJpaEntity entity);

    RoomOptionJpaEntity toJpaEntity(RoomOption domain);

    @AfterMapping
    default void restoreRoomStatus(@MappingTarget Room room, RoomJpaEntity entity) {
        if (entity.getStatus() != null) {
            room.restoreStatus(entity.getStatus());
        }
    }

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
