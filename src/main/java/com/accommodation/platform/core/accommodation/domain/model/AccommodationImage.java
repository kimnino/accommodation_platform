package com.accommodation.platform.core.accommodation.domain.model;

import com.accommodation.platform.core.accommodation.domain.enums.ImageCategory;

import lombok.Getter;

@Getter
public class AccommodationImage {

    private final String relativePath;
    private final ImageCategory category;
    private final int displayOrder;
    private final boolean isPrimary;

    public AccommodationImage(String relativePath, ImageCategory category, int displayOrder, boolean isPrimary) {

        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("이미지 경로는 필수입니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("이미지 카테고리는 필수입니다.");
        }
        this.relativePath = relativePath;
        this.category = category;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
    }
}
