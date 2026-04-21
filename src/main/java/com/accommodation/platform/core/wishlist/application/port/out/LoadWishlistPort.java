package com.accommodation.platform.core.wishlist.application.port.out;

import java.util.List;

import com.accommodation.platform.core.wishlist.domain.model.Wishlist;

public interface LoadWishlistPort {

    /**
     * 회원 ID로 위시리스트 목록을 조회한다.
     *
     * @param memberId 회원 ID
     * @return 위시리스트 목록
     */
    List<Wishlist> findByMemberId(Long memberId);

    /**
     * 회원 ID와 숙소 ID로 위시리스트 존재 여부를 확인한다.
     *
     * @param memberId        회원 ID
     * @param accommodationId 숙소 ID
     * @return 존재하면 true
     */
    boolean existsByMemberIdAndAccommodationId(Long memberId, Long accommodationId);
}
