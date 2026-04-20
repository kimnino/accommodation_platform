package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.core.accommodation.domain.enums.ImageCategory;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙소 이미지 테이블.
 * 이미지 경로는 상대경로로 저장 (예: /accommodation/exterior/20260420102838.png).
 * 이미지 저장소 변경 시 base URL만 교체하면 됨.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_image")
public class AccommodationImageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 숙소 ID (FK 미사용, 인덱스로 조인)
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 이미지 상대경로 (예: /accommodation/exterior/20260420102838.png)
     */
    @Column(nullable = false)
    private String relativePath;

    /**
     * 이미지 카테고리 (EXTERIOR, LOBBY, ROOM 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageCategory category;

    /**
     * 노출 순서
     */
    private int displayOrder;

    /**
     * 대표 이미지 여부
     */
    private boolean isPrimary;

    public AccommodationImageJpaEntity(Long accommodationId, String relativePath, ImageCategory category,
                                       int displayOrder, boolean isPrimary) {

        this.accommodationId = accommodationId;
        this.relativePath = relativePath;
        this.category = category;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
    }
}
