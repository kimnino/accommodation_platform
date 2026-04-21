package com.accommodation.platform.core.wishlist.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistJpaRepository extends JpaRepository<WishlistJpaEntity, Long> {

    List<WishlistJpaEntity> findByMemberIdAndIsDeletedFalse(Long memberId);

    boolean existsByMemberIdAndAccommodationIdAndIsDeletedFalse(Long memberId, Long accommodationId);

    Optional<WishlistJpaEntity> findByMemberIdAndAccommodationIdAndIsDeletedFalse(Long memberId, Long accommodationId);
}
