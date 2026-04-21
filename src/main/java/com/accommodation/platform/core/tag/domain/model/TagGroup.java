package com.accommodation.platform.core.tag.domain.model;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;

@Getter
public class TagGroup extends BaseEntity {

    private final Long id;
    private String name;
    private int displayOrder;
    private final TagTarget targetType;
    private AccommodationType accommodationType;
    private Long supplierId;
    private boolean isActive;

    @Builder
    public TagGroup(Long id, String name, int displayOrder,
                    TagTarget targetType, AccommodationType accommodationType, Long supplierId) {

        validateRequired(name, targetType);
        this.id = id;
        this.name = name;
        this.displayOrder = displayOrder;
        this.targetType = targetType;
        this.accommodationType = accommodationType;
        this.supplierId = supplierId;
        this.isActive = true;
        initTimestamps();
    }

    public void updateInfo(String name, Integer displayOrder, AccommodationType accommodationType) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (displayOrder != null) {
            this.displayOrder = displayOrder;
        }
        this.accommodationType = accommodationType;
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

    private void validateRequired(String name, TagTarget targetType) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("태그 그룹명은 필수입니다.");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("태그 대상 유형은 필수입니다.");
        }
    }
}
