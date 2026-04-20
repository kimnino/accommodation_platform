package com.accommodation.platform.core.tag.adapter.out.persistence;

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
 * 태그 다국어 테이블.
 * 태그명의 언어별 번역을 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "tag_translation")
public class TagTranslationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 태그 ID */
    @Column(nullable = false)
    private Long tagId;

    /** 언어코드 (ko, en, ja) */
    @Column(nullable = false, length = 10)
    private String locale;

    /** 태그명 — 언어별 번역 */
    @Column(nullable = false)
    private String name;

    public TagTranslationJpaEntity(Long tagId, String locale, String name) {

        this.tagId = tagId;
        this.locale = locale.toLowerCase();
        this.name = name;
    }
}
