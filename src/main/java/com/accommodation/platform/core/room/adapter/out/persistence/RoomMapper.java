package com.accommodation.platform.core.room.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;

@Component
public class RoomMapper {

    public Room toDomain(RoomJpaEntity entity) {

        Room room = Room.builder()
                .id(entity.getId())
                .accommodationId(entity.getAccommodationId())
                .name(entity.getName())
                .roomTypeName(entity.getRoomTypeName())
                .standardCapacity(entity.getStandardCapacity())
                .maxCapacity(entity.getMaxCapacity())
                .build();

        room.setCreatedAt(entity.getCreatedAt());
        room.setUpdatedAt(entity.getUpdatedAt());

        return room;
    }

    public RoomJpaEntity toJpaEntity(Room domain) {

        return new RoomJpaEntity(
                domain.getId(),
                domain.getAccommodationId(),
                domain.getName(),
                domain.getRoomTypeName(),
                domain.getStandardCapacity(),
                domain.getMaxCapacity(),
                domain.getStatus());
    }

    public RoomOption toDomain(RoomOptionJpaEntity entity) {

        RoomOption option = RoomOption.builder()
                .id(entity.getId())
                .roomId(entity.getRoomId())
                .name(entity.getName())
                .cancellationPolicy(entity.getCancellationPolicy())
                .additionalPrice(entity.getAdditionalPrice())
                .build();

        option.setCreatedAt(entity.getCreatedAt());
        option.setUpdatedAt(entity.getUpdatedAt());

        return option;
    }

    public RoomOptionJpaEntity toJpaEntity(RoomOption domain) {

        return new RoomOptionJpaEntity(
                domain.getId(),
                domain.getRoomId(),
                domain.getName(),
                domain.getCancellationPolicy(),
                domain.getAdditionalPrice());
    }
}
