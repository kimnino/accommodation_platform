package com.accommodation.platform.core.review.domain.model;

/**
 * 리뷰 이미지 VO.
 * 이미지는 상대 경로로만 저장한다 (예: /review/20260420/abc.png).
 */
public record ReviewImage(

    /** 이미지 상대 경로 */
    String relativePath,

    /** 노출 순서 */
    int displayOrder
) {
    public ReviewImage {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("이미지 경로는 필수입니다.");
        }
        if (displayOrder < 0) {
            throw new IllegalArgumentException("노출 순서는 0 이상이어야 합니다.");
        }
    }
}
