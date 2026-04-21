package com.accommodation.platform.core.coupon.application.port.out;

import com.accommodation.platform.core.coupon.domain.model.Coupon;
import com.accommodation.platform.core.coupon.domain.model.MemberCoupon;

public interface PersistCouponPort {

    /**
     * 쿠폰을 저장(신규 생성 또는 수정)한다.
     *
     * @param coupon 저장할 쿠폰
     * @return 저장된 쿠폰
     */
    Coupon save(Coupon coupon);

    /**
     * 회원 쿠폰을 저장(신규 발급 또는 사용 상태 변경)한다.
     *
     * @param memberCoupon 저장할 회원 쿠폰
     * @return 저장된 회원 쿠폰
     */
    MemberCoupon saveMemberCoupon(MemberCoupon memberCoupon);
}
