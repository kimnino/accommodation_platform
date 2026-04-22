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
 * 객실-태그 매핑 테이블.
 * 객실에 연결된 태그 목록을 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room_tag")
public class RoomTagJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 객실 ID
     */
    @Column(nullable = false)
    private Long roomId;

    /**
     * 태그 ID
     */
    @Column(nullable = false)
    private Long tagId;

    public RoomTagJpaEntity(Long roomId, Long tagId) {

        this.roomId = roomId;
        this.tagId = tagId;
    }
}
