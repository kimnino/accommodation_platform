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
 * 숙소-태그 매핑 테이블.
 * 숙소에 연결된 태그 목록을 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_tag")
public class AccommodationTagJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 숙소 ID
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 태그 ID
     */
    @Column(nullable = false)
    private Long tagId;

    public AccommodationTagJpaEntity(Long accommodationId, Long tagId) {

        this.accommodationId = accommodationId;
        this.tagId = tagId;
    }
}
