package com.accommodation.platform.core.review.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, Long> {

    Optional<ReviewJpaEntity> findByIdAndIsDeletedFalse(Long id);

    List<ReviewJpaEntity> findByAccommodationIdAndIsDeletedFalse(Long accommodationId, Pageable pageable);
}
