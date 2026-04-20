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
 * 외부 객실옵션 → 내부 객실옵션 매핑 테이블.
 * 외부 업체가 3단 구조(숙소→객실→옵션)일 때 사용.
 * 2단 구조 업체는 이 매핑 없이 supplier_room_mapping의 roomId로 기본 옵션에 자동 연결.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "supplier_room_option_mapping")
public class SupplierRoomOptionMappingJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공급사 ID */
    @Column(nullable = false)
    private Long supplierId;

    /** 외부 객실옵션 ID (공급사 기준) */
    @Column(nullable = false)
    private String externalRoomOptionId;

    /** 내부 객실옵션 ID */
    @Column(nullable = false)
    private Long roomOptionId;

    public SupplierRoomOptionMappingJpaEntity(Long supplierId, String externalRoomOptionId, Long roomOptionId) {

        this.supplierId = supplierId;
        this.externalRoomOptionId = externalRoomOptionId;
        this.roomOptionId = roomOptionId;
    }
}
