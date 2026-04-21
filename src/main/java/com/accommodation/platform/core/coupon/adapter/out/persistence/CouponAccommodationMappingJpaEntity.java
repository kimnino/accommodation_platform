package com.accommodation.platform.core.coupon.adapter.out.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import static lombok.AccessLevel.PROTECTED;

/**
 * 쿠폰-숙소 매핑 테이블.
 * 특정 쿠폰이 적용 가능한 숙소를 제한할 때 사용한다.
 * 매핑이 없으면 전체 숙소에 적용 가능으로 간주한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "coupon_accommodation_mapping",
    indexes = {
        @Index(name = "idx_coupon_accommodation_coupon_id", columnList = "couponId"),
        @Index(name = "idx_coupon_accommodation_accommodation_id", columnList = "accommodationId")
    }
)
public class CouponAccommodationMappingJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 쿠폰 ID (coupon 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long couponId;

    /**
     * 숙소 ID (accommodation 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    public CouponAccommodationMappingJpaEntity(Long id, Long couponId, Long accommodationId) {
        this.id = id;
        this.couponId = couponId;
        this.accommodationId = accommodationId;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
