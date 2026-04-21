package com.accommodation.platform.core.wishlist.application.port.out;

import com.accommodation.platform.core.wishlist.domain.model.Wishlist;

public interface PersistWishlistPort {

    /**
     * 위시리스트를 저장(추가)한다.
     *
     * @param wishlist 저장할 위시리스트
     * @return 저장된 위시리스트
     */
    Wishlist save(Wishlist wishlist);

    /**
     * 회원 ID와 숙소 ID로 위시리스트를 삭제한다.
     *
     * @param memberId        회원 ID
     * @param accommodationId 숙소 ID
     */
    void deleteByMemberIdAndAccommodationId(Long memberId, Long accommodationId);
}
