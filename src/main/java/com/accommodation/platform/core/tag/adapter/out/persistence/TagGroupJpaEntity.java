package com.accommodation.platform.core.tag.adapter.out.persistence;

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
import com.accommodation.platform.core.tag.domain.enums.TagTarget;

import static lombok.AccessLevel.PROTECTED;

/**
 * 태그 그룹 테이블.
 * 관리자가 숙소유형별로 태그 그룹을 등록/관리.
 * accommodationType이 null이면 모든 숙소유형에 적용(ALL).
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "tag_group")
public class TagGroupJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 태그 그룹명 (예: 공용시설, 취향, 서비스)
     */
    @Column(nullable = false)
    private String name;

    /**
     * 노출 순서
     */
    private int displayOrder;

    /**
     * 태그 적용 대상 (ACCOMMODATION 또는 ROOM)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagTarget targetType;

    /**
     * 대상 숙소유형 (null이면 모든 숙소유형에 적용)
     */
    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;

    /**
     * 공급사 ID (null이면 자체 플랫폼 태그, 값 있으면 해당 공급사 전용 태그)
     */
    private Long supplierId;

    /**
     * 활성 여부
     */
    private boolean isActive;

    public TagGroupJpaEntity(Long id, String name, int displayOrder,
                             TagTarget targetType, AccommodationType accommodationType,
                             Long supplierId, boolean isActive) {

        this.id = id;
        this.name = name;
        this.displayOrder = displayOrder;
        this.targetType = targetType;
        this.accommodationType = accommodationType;
        this.supplierId = supplierId;
        this.isActive = isActive;
    }
}
