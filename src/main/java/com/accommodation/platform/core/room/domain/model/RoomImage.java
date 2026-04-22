package com.accommodation.platform.core.room.domain.model;

public record RoomImage(
        Long roomId,
        String relativePath,
        int displayOrder,
        boolean isPrimary
) {}
