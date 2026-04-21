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
 * 회원 보유 쿠폰 테이블.
 * 회원에게 발급된 쿠폰과 사용 여부를 관리한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "member_coupon",
    indexes = {
        @Index(name = "idx_member_coupon_member_id", columnList = "memberId"),
        @Index(name = "idx_member_coupon_coupon_id", columnList = "couponId")
    }
)
public class MemberCouponJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 ID (member 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long memberId;

    /**
     * 쿠폰 ID (coupon 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long couponId;

    /**
     * 쿠폰 사용 여부
     */
    @Column(nullable = false)
    private boolean isUsed;

    /**
     * 쿠폰 사용 일시 (사용 전이면 null)
     */
    private Instant usedAt;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    public MemberCouponJpaEntity(Long id, Long memberId, Long couponId, boolean isUsed, Instant usedAt) {
        this.id = id;
        this.memberId = memberId;
        this.couponId = couponId;
        this.isUsed = isUsed;
        this.usedAt = usedAt;
    }

    public void markUsed(Instant usedAt) {
        this.isUsed = true;
        this.usedAt = usedAt;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
