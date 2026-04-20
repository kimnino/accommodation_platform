package com.accommodation.platform.core.room.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 객실 옵션 다국어 정보 테이블.
 * 하나의 객실 옵션에 대해 언어별(locale)로 옵션명을 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room_option_translation")
public class RoomOptionTranslationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 객실 옵션 ID (FK 미사용, 인덱스로 조인) */
    @Column(nullable = false)
    private Long roomOptionId;

    /** 언어코드 (ko, en, ja) — 소문자 */
    @Column(nullable = false, length = 10)
    private String locale;

    /** 옵션명 — 언어별 번역 (예: "조식 포함", "Breakfast included") */
    @Column(nullable = false)
    private String name;

    public RoomOptionTranslationJpaEntity(Long roomOptionId, String locale, String name) {

        this.roomOptionId = roomOptionId;
        this.locale = locale.toLowerCase();
        this.name = name;
    }
}
