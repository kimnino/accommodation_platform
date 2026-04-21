package com.accommodation.platform.core.wishlist.domain.model;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;

@Getter
public class Wishlist extends BaseEntity {

    private Long id;
    private final Long memberId;
    private final Long accommodationId;

    @Builder
    public Wishlist(Long id, Long memberId, Long accommodationId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("accommodationId는 필수입니다.");
        }
        this.id = id;
        this.memberId = memberId;
        this.accommodationId = accommodationId;
        initTimestamps();
    }
}
