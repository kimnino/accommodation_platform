package com.accommodation.platform.core.room.adapter.out.persistence;

import java.math.BigDecimal;

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
import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;

import static lombok.AccessLevel.PROTECTED;

/**
 * 객실 옵션 테이블.
 * 하나의 객실(room)에 여러 옵션이 속함.
 * 예: "스탠다드 - 조식 미포함", "스탠다드 - 조식 포함", "스탠다드 - 오션뷰" 등
 * 동일 객실의 상품 분기를 옵션명과 추가금액으로 표현.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room_option")
public class RoomOptionJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 객실 ID (FK 미사용, 인덱스로 조인)
     */
    @Column(nullable = false)
    private Long roomId;

    /**
     * 옵션명 (예: "조식 포함", "오션뷰", "레이트 체크아웃")
     */
    @Column(nullable = false)
    private String name;

    /**
     * 취소 정책 (FREE_CANCELLATION, NON_REFUNDABLE, PARTIAL_REFUND)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CancellationPolicy cancellationPolicy;

    /**
     * 기본 객실 가격 대비 추가 금액
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal additionalPrice;

    public RoomOptionJpaEntity(Long id, Long roomId, String name,
                               CancellationPolicy cancellationPolicy, BigDecimal additionalPrice) {

        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.cancellationPolicy = cancellationPolicy;
        this.additionalPrice = additionalPrice;
    }
}
