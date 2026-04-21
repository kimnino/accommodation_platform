package com.accommodation.platform.core.coupon.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.coupon.domain.enums.DiscountType;

import static lombok.AccessLevel.PROTECTED;

/**
 * 쿠폰 테이블.
 * 할인 유형(정액/정률), 유효 기간, 사용 한도를 관리한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "coupon",
    indexes = {
        @Index(name = "idx_coupon_code", columnList = "code")
    }
)
public class CouponJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 쿠폰 코드 (유일값)
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * 할인 유형 (FIXED / PERCENTAGE)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    /**
     * 할인 금액 또는 할인율
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal discountAmount;

    /**
     * 최소 주문 금액 (이 금액 이상 주문 시 적용 가능)
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal minimumOrderAmount;

    /**
     * 최대 할인 금액 (정률 할인 시 상한선, null이면 무제한)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal maximumDiscountAmount;

    /**
     * 쿠폰 유효 시작 일시 (UTC)
     */
    @Column(nullable = false)
    private Instant validFrom;

    /**
     * 쿠폰 유효 종료 일시 (UTC)
     */
    @Column(nullable = false)
    private Instant validTo;

    /**
     * 총 사용 한도 (0이면 무제한)
     */
    @Column(nullable = false)
    private int usageLimit;

    /**
     * 현재까지 사용된 횟수
     */
    @Column(nullable = false)
    private int usedCount;

    /**
     * 쿠폰 활성 여부
     */
    @Column(nullable = false)
    private boolean isActive;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    public CouponJpaEntity(Long id, String code, DiscountType discountType,
                           BigDecimal discountAmount, BigDecimal minimumOrderAmount,
                           BigDecimal maximumDiscountAmount, Instant validFrom, Instant validTo,
                           int usageLimit, int usedCount, boolean isActive) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.minimumOrderAmount = minimumOrderAmount;
        this.maximumDiscountAmount = maximumDiscountAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.isActive = isActive;
    }

    public void update(int usedCount, boolean isActive) {
        this.usedCount = usedCount;
        this.isActive = isActive;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
