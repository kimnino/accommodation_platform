package com.accommodation.platform.core.price.adapter.out.persistence;

import java.math.BigDecimal;
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
import com.accommodation.platform.core.price.domain.enums.PriceType;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 객실 요금 테이블.
 * roomOptionId + date + priceType 조합으로 일별/유형별 요금 관리.
 * 같은 날짜에 STAY와 HOURLY 가격이 각각 존재할 수 있음.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room_price")
public class RoomPriceJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 객실 옵션 ID */
    @Column(nullable = false)
    private Long roomOptionId;

    /** 날짜 */
    @Column(nullable = false)
    private LocalDate date;

    /** 가격 유형 (STAY: 숙박, HOURLY: 대실) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceType priceType;

    /** 기본 가격 (정가) */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    /** 판매 가격 (할인 적용 후) */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    /** 세금 포함 여부 */
    private boolean taxIncluded;

    public RoomPriceJpaEntity(Long id, Long roomOptionId, LocalDate date, PriceType priceType,
                               BigDecimal basePrice, BigDecimal sellingPrice, boolean taxIncluded) {

        this.id = id;
        this.roomOptionId = roomOptionId;
        this.date = date;
        this.priceType = priceType;
        this.basePrice = basePrice;
        this.sellingPrice = sellingPrice;
        this.taxIncluded = taxIncluded;
    }
}
