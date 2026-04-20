package com.accommodation.platform.core.tag.domain.model;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;

@Getter
public class Tag extends BaseEntity {

    private final Long id;
    private final Long tagGroupId;
    private String name;
    private int displayOrder;
    private boolean isActive;

    @Builder
    public Tag(Long id, Long tagGroupId, String name, int displayOrder) {

        validateRequired(tagGroupId, name);
        this.id = id;
        this.tagGroupId = tagGroupId;
        this.name = name;
        this.displayOrder = displayOrder;
        this.isActive = true;
        initTimestamps();
    }

    public void updateInfo(String name, int displayOrder) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.displayOrder = displayOrder;
        updateTimestamp();
    }

    public void activate() {

        this.isActive = true;
        updateTimestamp();
    }

    public void deactivate() {

        this.isActive = false;
        updateTimestamp();
    }

    private void validateRequired(Long tagGroupId, String name) {

        if (tagGroupId == null) {
            throw new IllegalArgumentException("tagGroupId는 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("태그명은 필수입니다.");
        }
    }
}
