package com.accommodation.platform.core.member.domain.model;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.member.domain.enums.MemberRole;
import com.accommodation.platform.core.member.domain.enums.MemberStatus;

@Getter
public class Member extends BaseEntity {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private final MemberRole role;
    private MemberStatus status;

    @Builder
    public Member(Long id, String name, String phone, String email, MemberRole role) {
        validateRequired(name, phone, email, role);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
        initTimestamps();
    }

    public void suspend() {
        if (this.status != MemberStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태에서만 정지할 수 있습니다.");
        }
        this.status = MemberStatus.SUSPENDED;
        updateTimestamp();
    }

    public void withdraw() {
        if (this.status == MemberStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }
        this.status = MemberStatus.WITHDRAWN;
        updateTimestamp();
    }

    public void reactivate() {
        if (this.status != MemberStatus.SUSPENDED) {
            throw new IllegalStateException("SUSPENDED 상태에서만 재활성화할 수 있습니다.");
        }
        this.status = MemberStatus.ACTIVE;
        updateTimestamp();
    }

    public void updateInfo(String name, String phone) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }
        updateTimestamp();
    }

    public void restoreStatus(MemberStatus status) {
        this.status = status;
    }

    private void validateRequired(String name, String phone, String email, MemberRole role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (role == null) {
            throw new IllegalArgumentException("역할은 필수입니다.");
        }
    }
}
