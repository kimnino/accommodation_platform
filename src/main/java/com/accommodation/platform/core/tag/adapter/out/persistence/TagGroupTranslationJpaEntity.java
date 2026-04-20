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
 * 태그 그룹 다국어 테이블.
 * 태그 그룹명의 언어별 번역을 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "tag_group_translation")
public class TagGroupTranslationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 태그 그룹 ID */
    @Column(nullable = false)
    private Long tagGroupId;

    /** 언어코드 (ko, en, ja) */
    @Column(nullable = false, length = 10)
    private String locale;

    /** 태그 그룹명 — 언어별 번역 */
    @Column(nullable = false)
    private String name;

    public TagGroupTranslationJpaEntity(Long tagGroupId, String locale, String name) {

        this.tagGroupId = tagGroupId;
        this.locale = locale.toLowerCase();
        this.name = name;
    }
}
