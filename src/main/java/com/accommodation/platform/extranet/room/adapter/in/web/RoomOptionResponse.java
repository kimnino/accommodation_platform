package com.accommodation.platform.extranet.room.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;

import com.accommodation.platform.core.room.domain.model.RoomOption;

public record RoomOptionResponse(
        Long id,
        Long roomId,
        String name,
        String cancellationPolicy,
        BigDecimal additionalPrice,
        Instant createdAt,
        Instant updatedAt
) {

    public static RoomOptionResponse from(RoomOption option) {

        return new RoomOptionResponse(
                option.getId(),
                option.getRoomId(),
                option.getName(),
                option.getCancellationPolicy().name(),
                option.getAdditionalPrice(),
                option.getCreatedAt(),
                option.getUpdatedAt());
    }
}
