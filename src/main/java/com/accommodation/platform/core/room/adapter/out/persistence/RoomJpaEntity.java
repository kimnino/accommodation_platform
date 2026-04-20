package com.accommodation.platform.core.room.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.room.domain.enums.RoomStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 객실 정보 테이블.
 * 하나의 숙소(accommodation)에 여러 객실이 속함.
 * 객실명/객실유형명의 다국어는 room_translation에서 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "room")
public class RoomJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 숙소 ID (FK 미사용, 인덱스로 조인) */
    @Column(nullable = false)
    private Long accommodationId;

    /** 객실명 (기본 언어). 다국어는 room_translation에서 관리 */
    @Column(nullable = false)
    private String name;

    /** 객실 유형명 (nullable). 파트너가 직접 입력, 다국어는 room_translation에서 관리 */
    private String roomTypeName;

    /** 기준 인원 */
    private int standardCapacity;

    /** 최대 인원 */
    private int maxCapacity;

    /** 객실 상태 (ACTIVE, INACTIVE) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    public RoomJpaEntity(Long id, Long accommodationId, String name, String roomTypeName,
                         int standardCapacity, int maxCapacity, RoomStatus status) {

        this.id = id;
        this.accommodationId = accommodationId;
        this.name = name;
        this.roomTypeName = roomTypeName;
        this.standardCapacity = standardCapacity;
        this.maxCapacity = maxCapacity;
        this.status = status;
    }
}
