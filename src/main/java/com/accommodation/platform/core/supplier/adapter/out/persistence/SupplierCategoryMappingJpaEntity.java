package com.accommodation.platform.core.supplier.adapter.out.persistence;

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

import static lombok.AccessLevel.PROTECTED;

/**
 * 공급사 카테고리 ↔ 플랫폼 내부 태그 매핑 테이블.
 * 공급사별 카테고리 코드를 우리 태그 시스템의 태그 ID로 변환하는 기준.
 * PENDING 상태는 관리자가 매핑 전 검토가 필요한 미매핑 항목.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "supplier_category_mapping")
public class SupplierCategoryMappingJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 공급사 ID (supplier 테이블 FK)
     */
    @Column(nullable = false)
    private Long supplierId;

    /**
     * 공급사 카테고리 그룹명 (예: 테마, 편의시설)
     */
    @Column(nullable = false)
    private String supplierGroup;

    /**
     * 공급사 카테고리 값 (예: 커플, 수영장)
     */
    @Column(nullable = false)
    private String supplierValue;

    /**
     * 내부 태그 ID (null이면 미매핑)
     */
    private Long internalTagId;

    /**
     * 매핑 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MappingStatus mappingStatus;

    public enum MappingStatus {
        MAPPED,
        PENDING,
        IGNORED
    }

    public SupplierCategoryMappingJpaEntity(Long supplierId, String supplierGroup,
                                             String supplierValue, Long internalTagId,
                                             MappingStatus mappingStatus) {
        this.supplierId = supplierId;
        this.supplierGroup = supplierGroup;
        this.supplierValue = supplierValue;
        this.internalTagId = internalTagId;
        this.mappingStatus = mappingStatus;
    }

    public void map(Long internalTagId) {
        this.internalTagId = internalTagId;
        this.mappingStatus = MappingStatus.MAPPED;
    }

    public void ignore() {
        this.mappingStatus = MappingStatus.IGNORED;
    }
}
