package com.accommodation.platform.core.room.adapter.out.persistence;

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
 * 객실 이미지 테이블.
 * 이미지 경로는 상대경로로 저장.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room_image")
public class RoomImageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 객실 ID */
    @Column(nullable = false)
    private Long roomId;

    /** 이미지 상대경로 */
    @Column(nullable = false)
    private String relativePath;

    /** 노출 순서 */
    private int displayOrder;

    /** 대표 이미지 여부 */
    private boolean isPrimary;

    public RoomImageJpaEntity(Long roomId, String relativePath, int displayOrder, boolean isPrimary) {

        this.roomId = roomId;
        this.relativePath = relativePath;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
    }
}
