package com.accommodation.platform.core.coupon.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.coupon.domain.enums.DiscountType;

@Getter
public class Coupon extends BaseEntity {

    private Long id;
    private String code;
    private final DiscountType discountType;
    private final BigDecimal discountAmount;
    private final BigDecimal minimumOrderAmount;
    private final BigDecimal maximumDiscountAmount;
    private final Instant validFrom;
    private final Instant validTo;
    private final int usageLimit;
    private int usedCount;
    private boolean isActive;

    @Builder
    public Coupon(Long id, String code, DiscountType discountType, BigDecimal discountAmount,
                  BigDecimal minimumOrderAmount, BigDecimal maximumDiscountAmount,
                  Instant validFrom, Instant validTo, int usageLimit) {
        validateRequired(code, discountType, discountAmount, validFrom, validTo);
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.minimumOrderAmount = minimumOrderAmount != null ? minimumOrderAmount : BigDecimal.ZERO;
        this.maximumDiscountAmount = maximumDiscountAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.usageLimit = usageLimit;
        this.usedCount = 0;
        this.isActive = true;
        initTimestamps();
    }

    public boolean isUsable(Instant now) {
        return isActive
            && !now.isBefore(validFrom)
            && !now.isAfter(validTo)
            && (usageLimit <= 0 || usedCount < usageLimit);
    }

    public void incrementUsedCount() {
        this.usedCount++;
        updateTimestamp();
    }

    public void deactivate() {
        this.isActive = false;
        updateTimestamp();
    }

    public void activate() {
        this.isActive = true;
        updateTimestamp();
    }

    public void restoreUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public void restoreActive(boolean isActive) {
        this.isActive = isActive;
    }

    private void validateRequired(String code, DiscountType discountType, BigDecimal discountAmount,
                                   Instant validFrom, Instant validTo) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("쿠폰 코드는 필수입니다.");
        }
        if (discountType == null) {
            throw new IllegalArgumentException("할인 유형은 필수입니다.");
        }
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("할인 금액은 0보다 커야 합니다.");
        }
        if (validFrom == null || validTo == null) {
            throw new IllegalArgumentException("유효 기간은 필수입니다.");
        }
        if (validFrom.isAfter(validTo)) {
            throw new IllegalArgumentException("유효 시작일은 종료일보다 이전이어야 합니다.");
        }
    }
}
