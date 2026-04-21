package com.accommodation.platform.core.review.application.port.out;

import com.accommodation.platform.core.review.domain.model.Review;

public interface PersistReviewPort {

    /**
     * 리뷰를 저장(신규 작성 또는 수정)한다.
     *
     * @param review 저장할 리뷰
     * @return 저장된 리뷰
     */
    Review save(Review review);
}
