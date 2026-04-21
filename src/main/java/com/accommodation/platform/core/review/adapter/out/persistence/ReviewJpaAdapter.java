package com.accommodation.platform.core.review.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.review.application.port.out.LoadReviewPort;
import com.accommodation.platform.core.review.application.port.out.PersistReviewPort;
import com.accommodation.platform.core.review.domain.enums.ReviewSortType;
import com.accommodation.platform.core.review.domain.model.Review;

@Component
@RequiredArgsConstructor
public class ReviewJpaAdapter implements LoadReviewPort, PersistReviewPort {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Optional<Review> findById(Long id) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public List<Review> findByAccommodationId(Long accommodationId, ReviewSortType sortType, int page, int size) {
        // TODO: implement
        return List.of();
    }

    @Override
    public Review save(Review review) {
        // TODO: implement
        return review;
    }
}
