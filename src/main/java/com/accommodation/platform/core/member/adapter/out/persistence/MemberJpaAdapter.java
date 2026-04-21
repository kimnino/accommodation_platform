package com.accommodation.platform.core.member.adapter.out.persistence;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.member.application.port.out.LoadMemberPort;
import com.accommodation.platform.core.member.application.port.out.PersistMemberPort;
import com.accommodation.platform.core.member.domain.model.Member;

@Component
@RequiredArgsConstructor
public class MemberJpaAdapter implements LoadMemberPort, PersistMemberPort {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long id) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public Member save(Member member) {
        // TODO: implement
        return member;
    }
}
