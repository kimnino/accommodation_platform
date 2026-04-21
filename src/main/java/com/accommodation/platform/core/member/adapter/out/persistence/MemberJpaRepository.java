package com.accommodation.platform.core.member.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByEmailAndIsDeletedFalse(String email);

    Optional<MemberJpaEntity> findByIdAndIsDeletedFalse(Long id);
}
