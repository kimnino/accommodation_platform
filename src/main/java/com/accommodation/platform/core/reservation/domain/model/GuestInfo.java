package com.accommodation.platform.core.reservation.domain.model;

public record GuestInfo(String name, String phone, String email) {

    public GuestInfo {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("투숙객 이름은 필수입니다.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("투숙객 연락처는 필수입니다.");
        }
    }
}
