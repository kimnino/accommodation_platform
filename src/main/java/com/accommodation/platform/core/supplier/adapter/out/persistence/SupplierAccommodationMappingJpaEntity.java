package com.accommodation.platform.core.supplier.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 외부 숙소 → 내부 숙소 매핑 테이블.
 * 하나의 외부 숙소가 내부 하나의 숙소에 대응.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "supplier_accommodation_mapping")
public class SupplierAccommodationMappingJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공급사 ID */
    @Column(nullable = false)
    private Long supplierId;

    /** 외부 숙소 ID (공급사 기준) */
    @Column(nullable = false)
    private String externalAccommodationId;

    /** 내부 숙소 ID */
    @Column(nullable = false)
    private Long accommodationId;

    /** 가격 실시간 연동 여부 (false면 관리자가 수동 가격 설정) */
    private boolean livePriceSync;

    public SupplierAccommodationMappingJpaEntity(Long supplierId, String externalAccommodationId,
                                                  Long accommodationId, boolean livePriceSync) {

        this.supplierId = supplierId;
        this.externalAccommodationId = externalAccommodationId;
        this.accommodationId = accommodationId;
        this.livePriceSync = livePriceSync;
    }
}
