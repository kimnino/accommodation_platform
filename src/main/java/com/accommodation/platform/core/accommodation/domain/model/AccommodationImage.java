package com.accommodation.platform.core.accommodation.domain.model;

import com.accommodation.platform.core.accommodation.domain.enums.ImageCategory;

public record AccommodationImage(String relativePath, ImageCategory category, int displayOrder, boolean isPrimary) {

    public AccommodationImage {

        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("이미지 경로는 필수입니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("이미지 카테고리는 필수입니다.");
        }
    }
}
