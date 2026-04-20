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
 * 외부 객실 → 내부 객실옵션 매핑 테이블.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "supplier_room_mapping")
public class SupplierRoomMappingJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공급사 ID */
    @Column(nullable = false)
    private Long supplierId;

    /** 외부 객실 ID (공급사 기준) */
    @Column(nullable = false)
    private String externalRoomId;

    /** 내부 객실 ID */
    @Column(nullable = false)
    private Long roomId;

    public SupplierRoomMappingJpaEntity(Long supplierId, String externalRoomId, Long roomId) {

        this.supplierId = supplierId;
        this.externalRoomId = externalRoomId;
        this.roomId = roomId;
    }
}
