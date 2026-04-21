package com.accommodation.platform.core.coupon.domain.model;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;

@Getter
public class CouponAccommodationMapping extends BaseEntity {

    private Long id;
    private final Long couponId;
    private final Long accommodationId;

    @Builder
    public CouponAccommodationMapping(Long id, Long couponId, Long accommodationId) {
        if (couponId == null) {
            throw new IllegalArgumentException("couponId는 필수입니다.");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("accommodationId는 필수입니다.");
        }
        this.id = id;
        this.couponId = couponId;
        this.accommodationId = accommodationId;
        initTimestamps();
    }
}
