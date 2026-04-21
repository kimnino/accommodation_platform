package com.accommodation.platform.core.review.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.review.domain.enums.ReviewSortType;
import com.accommodation.platform.core.review.domain.model.Review;

public interface LoadReviewPort {

    /**
     * ID로 리뷰를 조회한다.
     *
     * @param id 리뷰 ID
     * @return 리뷰 (없으면 empty)
     */
    Optional<Review> findById(Long id);

    /**
     * 숙소 ID로 리뷰 목록을 페이징 조회한다.
     *
     * @param accommodationId 숙소 ID
     * @param sortType        정렬 기준
     * @param page            페이지 번호 (0-based)
     * @param size            페이지 크기
     * @return 리뷰 목록
     */
    List<Review> findByAccommodationId(Long accommodationId, ReviewSortType sortType, int page, int size);
}
