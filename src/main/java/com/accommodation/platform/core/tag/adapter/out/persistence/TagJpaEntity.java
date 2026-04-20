package com.accommodation.platform.core.tag.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import static lombok.AccessLevel.PROTECTED;

/**
 * 태그 테이블.
 * 하나의 태그 그룹(tag_group)에 여러 태그가 속함.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "tag")
public class TagJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 태그 그룹 ID (FK 미사용, 인덱스로 조인)
     */
    @Column(nullable = false)
    private Long tagGroupId;

    /**
     * 태그명 (예: 수영장, 사우나, #가족여행숙소)
     */
    @Column(nullable = false)
    private String name;

    /**
     * 노출 순서
     */
    private int displayOrder;

    /**
     * 활성 여부
     */
    private boolean isActive;

    public TagJpaEntity(Long id, Long tagGroupId, String name, int displayOrder, boolean isActive) {

        this.id = id;
        this.tagGroupId = tagGroupId;
        this.name = name;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }
}
