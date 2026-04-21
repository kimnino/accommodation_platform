package com.accommodation.platform.core.review.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import static lombok.AccessLevel.PROTECTED;

/**
 * 리뷰 테이블.
 * 고객이 예약 완료 후 작성하는 숙소 리뷰 정보를 관리한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "review",
    indexes = {
        @Index(name = "idx_review_accommodation_id", columnList = "accommodationId"),
        @Index(name = "idx_review_member_id", columnList = "memberId"),
        @Index(name = "idx_review_reservation_id", columnList = "reservationId")
    }
)
public class ReviewJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 예약 ID (reservation 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long reservationId;

    /**
     * 작성자 회원 ID (member 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long memberId;

    /**
     * 대상 숙소 ID (accommodation 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 평점 (1.0 ~ 5.0)
     */
    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal rating;

    /**
     * 리뷰 본문
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 리뷰 노출 여부 (관리자가 숨길 수 있음)
     */
    @Column(nullable = false)
    private boolean isVisible;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ReviewImageJpaEntity> images = new ArrayList<>();

    public ReviewJpaEntity(Long id, Long reservationId, Long memberId, Long accommodationId,
                           BigDecimal rating, String content, boolean isVisible) {
        this.id = id;
        this.reservationId = reservationId;
        this.memberId = memberId;
        this.accommodationId = accommodationId;
        this.rating = rating;
        this.content = content;
        this.isVisible = isVisible;
    }

    public void update(BigDecimal rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    public void updateVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
