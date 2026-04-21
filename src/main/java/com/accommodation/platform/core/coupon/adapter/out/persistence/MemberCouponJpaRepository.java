package com.accommodation.platform.core.coupon.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCouponJpaEntity, Long> {

    List<MemberCouponJpaEntity> findByMemberIdAndIsUsedFalseAndIsDeletedFalse(Long memberId);
}
