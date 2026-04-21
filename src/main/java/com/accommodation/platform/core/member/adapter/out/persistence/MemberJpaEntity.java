package com.accommodation.platform.core.member.adapter.out.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.member.domain.enums.MemberRole;
import com.accommodation.platform.core.member.domain.enums.MemberStatus;

import static lombok.AccessLevel.PROTECTED;

/**
 * 회원 테이블.
 * 예약에 필요한 최소 식별 정보(이름, 연락처, 이메일)만 보유한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "member",
    indexes = {
        @Index(name = "idx_member_email", columnList = "email")
    }
)
public class MemberJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 연락처 (전화번호)
     */
    @Column(nullable = false)
    private String phone;

    /**
     * 이메일 주소 (로그인 식별자, 유일값)
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 회원 역할 (CUSTOMER / PARTNER / ADMIN)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    /**
     * 회원 상태 (ACTIVE / SUSPENDED / WITHDRAWN)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    public MemberJpaEntity(Long id, String name, String phone, String email,
                           MemberRole role, MemberStatus status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public void update(String name, String phone, MemberStatus status) {
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
