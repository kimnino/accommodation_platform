package com.accommodation.platform.core.coupon.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.coupon.application.port.out.LoadCouponPort;
import com.accommodation.platform.core.coupon.application.port.out.PersistCouponPort;
import com.accommodation.platform.core.coupon.domain.model.Coupon;
import com.accommodation.platform.core.coupon.domain.model.MemberCoupon;

@Component
@RequiredArgsConstructor
public class CouponJpaAdapter implements LoadCouponPort, PersistCouponPort {

    private final CouponJpaRepository couponJpaRepository;
    private final MemberCouponJpaRepository memberCouponJpaRepository;

    @Override
    public Optional<Coupon> findById(Long id) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public List<MemberCoupon> findActiveCouponsByMemberId(Long memberId) {
        // TODO: implement
        return List.of();
    }

    @Override
    public Coupon save(Coupon coupon) {
        // TODO: implement
        return coupon;
    }

    @Override
    public MemberCoupon saveMemberCoupon(MemberCoupon memberCoupon) {
        // TODO: implement
        return memberCoupon;
    }
}
