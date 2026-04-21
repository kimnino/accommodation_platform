package com.accommodation.platform.admin.tag.adapter.in.web;

import java.time.Instant;

import com.accommodation.platform.core.tag.domain.model.TagGroup;

public record TagGroupResponse(
        Long id,
        String name,
        int displayOrder,
        String targetType,
        String accommodationType,
        Long supplierId,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {

    public static TagGroupResponse from(TagGroup tagGroup) {

        return new TagGroupResponse(
                tagGroup.getId(),
                tagGroup.getName(),
                tagGroup.getDisplayOrder(),
                tagGroup.getTargetType().name(),
                tagGroup.getAccommodationType() != null ? tagGroup.getAccommodationType().name() : null,
                tagGroup.getSupplierId(),
                tagGroup.isActive(),
                tagGroup.getCreatedAt(),
                tagGroup.getUpdatedAt());
    }
}
