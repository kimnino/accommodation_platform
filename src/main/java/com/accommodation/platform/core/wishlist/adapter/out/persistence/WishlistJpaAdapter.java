package com.accommodation.platform.core.wishlist.adapter.out.persistence;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.wishlist.application.port.out.LoadWishlistPort;
import com.accommodation.platform.core.wishlist.application.port.out.PersistWishlistPort;
import com.accommodation.platform.core.wishlist.domain.model.Wishlist;

@Component
@RequiredArgsConstructor
public class WishlistJpaAdapter implements LoadWishlistPort, PersistWishlistPort {

    private final WishlistJpaRepository wishlistJpaRepository;

    @Override
    public List<Wishlist> findByMemberId(Long memberId) {
        // TODO: implement
        return List.of();
    }

    @Override
    public boolean existsByMemberIdAndAccommodationId(Long memberId, Long accommodationId) {
        // TODO: implement
        return false;
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        // TODO: implement
        return wishlist;
    }

    @Override
    public void deleteByMemberIdAndAccommodationId(Long memberId, Long accommodationId) {
        // TODO: implement
    }
}
