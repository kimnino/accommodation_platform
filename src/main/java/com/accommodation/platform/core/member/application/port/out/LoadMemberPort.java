package com.accommodation.platform.core.member.application.port.out;

import java.util.Optional;

import com.accommodation.platform.core.member.domain.model.Member;

public interface LoadMemberPort {

    /**
     * ID로 회원을 조회한다.
     *
     * @param id 회원 ID
     * @return 회원 (없으면 empty)
     */
    Optional<Member> findById(Long id);

    /**
     * 이메일로 회원을 조회한다.
     *
     * @param email 이메일 주소
     * @return 회원 (없으면 empty)
     */
    Optional<Member> findByEmail(String email);
}
