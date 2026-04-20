package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙박 재고 테이블.
 * roomOptionId + date 조합으로 일별 재고를 관리.
 * 예약 시 비관적 락(SELECT FOR UPDATE)으로 동시성 제어.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "inventory")
public class InventoryJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 객실 옵션 ID (FK 미사용, 인덱스로 조인) */
    @Column(nullable = false)
    private Long roomOptionId;

    /** 날짜 */
    @Column(nullable = false)
    private LocalDate date;

    /** 총 재고 수량 */
    private int totalQuantity;

    /** 잔여 재고 수량 (불변식: remaining >= 0) */
    private int remainingQuantity;

    /** 재고 상태 (AVAILABLE, SOLD_OUT, CLOSED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;

    public InventoryJpaEntity(Long id, Long roomOptionId, LocalDate date,
                               int totalQuantity, int remainingQuantity, InventoryStatus status) {

        this.id = id;
        this.roomOptionId = roomOptionId;
        this.date = date;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.status = status;
    }
}
