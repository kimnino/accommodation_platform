package com.accommodation.platform.core.coupon.domain.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;

@Getter
public class MemberCoupon extends BaseEntity {

    private Long id;
    private final Long memberId;
    private final Long couponId;
    private boolean isUsed;
    private Instant usedAt;

    @Builder
    public MemberCoupon(Long id, Long memberId, Long couponId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
        if (couponId == null) {
            throw new IllegalArgumentException("couponId는 필수입니다.");
        }
        this.id = id;
        this.memberId = memberId;
        this.couponId = couponId;
        this.isUsed = false;
        initTimestamps();
    }

    public void use() {
        if (this.isUsed) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.isUsed = true;
        this.usedAt = Instant.now();
        updateTimestamp();
    }

    public void restoreUsed(boolean isUsed, Instant usedAt) {
        this.isUsed = isUsed;
        this.usedAt = usedAt;
    }
}
