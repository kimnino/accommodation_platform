package com.accommodation.platform.core.member.application.port.out;

import com.accommodation.platform.core.member.domain.model.Member;

public interface PersistMemberPort {

    /**
     * 회원을 저장(신규 생성 또는 수정)한다.
     *
     * @param member 저장할 회원
     * @return 저장된 회원
     */
    Member save(Member member);
}
