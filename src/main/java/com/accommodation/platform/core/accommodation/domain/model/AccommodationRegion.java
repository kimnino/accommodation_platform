package com.accommodation.platform.core.accommodation.domain.model;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccommodationRegion {

    private Long id;
    private final AccommodationType accommodationType;
    private String regionName;
    private Long parentId;
    private int sortOrder;

    @Builder
    public AccommodationRegion(Long id, AccommodationType accommodationType,
                                String regionName, Long parentId, int sortOrder) {

        if (accommodationType == null) throw new IllegalArgumentException("숙소 유형은 필수입니다.");
        if (regionName == null || regionName.isBlank()) throw new IllegalArgumentException("지역명은 필수입니다.");
        this.id = id;
        this.accommodationType = accommodationType;
        this.regionName = regionName;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
    }

    public void update(String regionName, Long parentId, int sortOrder) {
        if (regionName != null && !regionName.isBlank()) {
            this.regionName = regionName;
        }
        this.parentId = parentId;
        this.sortOrder = sortOrder;
    }
}
