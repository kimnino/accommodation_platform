package com.accommodation.platform.core.coupon.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.coupon.domain.model.Coupon;
import com.accommodation.platform.core.coupon.domain.model.MemberCoupon;

public interface LoadCouponPort {

    /**
     * ID로 쿠폰을 조회한다.
     *
     * @param id 쿠폰 ID
     * @return 쿠폰 (없으면 empty)
     */
    Optional<Coupon> findById(Long id);

    /**
     * 쿠폰 코드로 쿠폰을 조회한다.
     *
     * @param code 쿠폰 코드
     * @return 쿠폰 (없으면 empty)
     */
    Optional<Coupon> findByCode(String code);

    /**
     * 회원이 보유한 활성 쿠폰 목록을 조회한다.
     *
     * @param memberId 회원 ID
     * @return 사용 가능한 회원 쿠폰 목록
     */
    List<MemberCoupon> findActiveCouponsByMemberId(Long memberId);
}
