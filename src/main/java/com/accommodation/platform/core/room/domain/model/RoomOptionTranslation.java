package com.accommodation.platform.core.room.domain.model;

public record RoomOptionTranslation(
        Long roomOptionId,
        String locale,
        String name
) {}
