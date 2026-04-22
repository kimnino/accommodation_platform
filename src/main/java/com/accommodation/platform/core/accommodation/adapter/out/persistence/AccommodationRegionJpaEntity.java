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

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙소 유형별 지역 마스터 테이블.
 * 계층 구조(parent_id)로 시/구/동 단위 지역을 유형별로 관리.
 * 같은 행정구역도 유형마다 다른 표현(강남구 vs 잠실/역삼)을 가질 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_region")
public class AccommodationRegionJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 숙소 유형 (HOTEL, MOTEL 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType accommodationType;

    /**
     * 지역명 (예: 서울, 강남구, 잠실)
     */
    @Column(nullable = false)
    private String regionName;

    /**
     * 상위 지역 ID. null이면 최상위 지역
     */
    private Long parentId;

    /**
     * 정렬 순서
     */
    @Column(nullable = false)
    private int sortOrder;

    public AccommodationRegionJpaEntity(AccommodationType accommodationType, String regionName,
                                         Long parentId, int sortOrder) {
        this.accommodationType = accommodationType;
        this.regionName = regionName;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
    }

    public void update(String regionName, Long parentId, int sortOrder) {
        this.regionName = regionName;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
    }
}
