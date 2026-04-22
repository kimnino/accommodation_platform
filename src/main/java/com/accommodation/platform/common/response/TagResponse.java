package com.accommodation.platform.common.response;

import java.time.Instant;

import com.accommodation.platform.core.tag.domain.model.Tag;

public record TagResponse(
        Long id,
        Long tagGroupId,
        String name,
        int displayOrder,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {

    public static TagResponse from(Tag tag) {

        return new TagResponse(
                tag.getId(),
                tag.getTagGroupId(),
                tag.getName(),
                tag.getDisplayOrder(),
                tag.isActive(),
                tag.getCreatedAt(),
                tag.getUpdatedAt());
    }
}
