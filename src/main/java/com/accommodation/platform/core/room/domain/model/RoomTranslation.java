package com.accommodation.platform.core.room.domain.model;

public record RoomTranslation(
        Long roomId,
        String locale,
        String name,
        String roomTypeName
) {}
