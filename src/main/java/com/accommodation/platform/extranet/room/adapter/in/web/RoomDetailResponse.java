package com.accommodation.platform.extranet.room.adapter.in.web;

import java.time.Instant;

import com.accommodation.platform.core.room.domain.model.Room;

public record RoomDetailResponse(
        Long id,
        Long accommodationId,
        String name,
        String roomTypeName,
        int standardCapacity,
        int maxCapacity,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static RoomDetailResponse from(Room room) {

        return new RoomDetailResponse(
                room.getId(),
                room.getAccommodationId(),
                room.getName(),
                room.getRoomTypeName(),
                room.getStandardCapacity(),
                room.getMaxCapacity(),
                room.getStatus().name(),
                room.getCreatedAt(),
                room.getUpdatedAt());
    }
}
