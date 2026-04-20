package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙소별 다국어 지원 언어 테이블.
 * 파트너가 자신의 숙소에 대해 어떤 언어를 지원할지 선택.
 * 언어코드는 소문자로 저장 (ko, en, ja).
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_supported_locale")
public class AccommodationSupportedLocaleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 숙소 ID
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 지원 언어코드 (ko, en, ja) — 소문자
     */
    @Column(nullable = false, length = 10)
    private String locale;

    public AccommodationSupportedLocaleJpaEntity(Long accommodationId, String locale) {

        this.accommodationId = accommodationId;
        this.locale = locale.toLowerCase();
    }
}
