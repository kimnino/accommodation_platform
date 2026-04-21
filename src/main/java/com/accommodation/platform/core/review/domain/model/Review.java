package com.accommodation.platform.core.review.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;

@Getter
public class Review extends BaseEntity {

    private static final BigDecimal MIN_RATING = new BigDecimal("1.0");
    private static final BigDecimal MAX_RATING = new BigDecimal("5.0");

    private Long id;
    private final Long reservationId;
    private final Long memberId;
    private final Long accommodationId;
    private BigDecimal rating;
    private String content;
    private boolean isVisible;
    private final List<ReviewImage> images = new ArrayList<>();

    @Builder
    public Review(Long id, Long reservationId, Long memberId, Long accommodationId,
                  BigDecimal rating, String content) {
        validateRequired(reservationId, memberId, accommodationId, rating, content);
        this.id = id;
        this.reservationId = reservationId;
        this.memberId = memberId;
        this.accommodationId = accommodationId;
        this.rating = rating;
        this.content = content;
        this.isVisible = true;
        initTimestamps();
    }

    public void update(BigDecimal rating, String content) {
        if (rating != null) {
            validateRating(rating);
            this.rating = rating;
        }
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
        updateTimestamp();
    }

    public void hide() {
        this.isVisible = false;
        updateTimestamp();
    }

    public void show() {
        this.isVisible = true;
        updateTimestamp();
    }

    public void addImage(ReviewImage image) {
        this.images.add(image);
        updateTimestamp();
    }

    public List<ReviewImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void restoreImages(List<ReviewImage> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    public void restoreVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    private void validateRequired(Long reservationId, Long memberId, Long accommodationId,
                                   BigDecimal rating, String content) {
        if (reservationId == null) {
            throw new IllegalArgumentException("reservationId는 필수입니다.");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("accommodationId는 필수입니다.");
        }
        validateRating(rating);
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
        }
    }

    private void validateRating(BigDecimal rating) {
        if (rating == null) {
            throw new IllegalArgumentException("평점은 필수입니다.");
        }
        if (rating.compareTo(MIN_RATING) < 0 || rating.compareTo(MAX_RATING) > 0) {
            throw new IllegalArgumentException("평점은 1.0 이상 5.0 이하여야 합니다.");
        }
    }
}
