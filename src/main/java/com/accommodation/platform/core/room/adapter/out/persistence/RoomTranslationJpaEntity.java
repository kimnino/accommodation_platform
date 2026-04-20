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
 * 객실 다국어 정보 테이블.
 * 하나의 객실에 대해 언어별(locale)로 객실명, 객실유형명을 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room_translation")
public class RoomTranslationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 객실 ID (FK 미사용, 인덱스로 조인) */
    @Column(nullable = false)
    private Long roomId;

    /** 언어 코드 (ko, en, ja, zh 등) */
    @Column(nullable = false, length = 10)
    private String locale;

    /** 객실명 — 언어별 번역 */
    private String name;

    /** 객실 유형명 — 언어별 번역 (파트너 직접 입력) */
    private String roomTypeName;

    public RoomTranslationJpaEntity(Long roomId, String locale, String name, String roomTypeName) {

        this.roomId = roomId;
        this.locale = locale;
        this.name = name;
        this.roomTypeName = roomTypeName;
    }
}
