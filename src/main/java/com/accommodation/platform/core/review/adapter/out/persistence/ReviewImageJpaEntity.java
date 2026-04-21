package com.accommodation.platform.core.review.adapter.out.persistence;

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
 * 리뷰 이미지 테이블.
 * 리뷰에 첨부된 이미지의 상대 경로와 노출 순서를 관리한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "review_image",
    indexes = {
        @Index(name = "idx_review_image_review_id", columnList = "reviewId")
    }
)
public class ReviewImageJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 리뷰 ID (review 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long reviewId;

    /**
     * 이미지 상대 경로 (예: /review/20260420/abc.png)
     */
    @Column(nullable = false)
    private String relativePath;

    /**
     * 노출 순서
     */
    @Column(nullable = false)
    private int displayOrder;

    public ReviewImageJpaEntity(Long reviewId, String relativePath, int displayOrder) {
        this.reviewId = reviewId;
        this.relativePath = relativePath;
        this.displayOrder = displayOrder;
    }
}
