package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.inventory.domain.enums.TimeSlotStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 대실 30분 단위 타임 블록 테이블.
 * slotTime이 11:30이면 11:30~12:00 구간을 의미.
 * 예약 시 연속 블록을 OCCUPIED로, 버퍼 블록을 BLOCKED로 변경.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "time_slot_inventory")
public class TimeSlotInventoryJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 객실 옵션 ID */
    @Column(nullable = false)
    private Long roomOptionId;

    /** 날짜 */
    @Column(nullable = false)
    private LocalDate date;

    /** 슬롯 시작 시간 (30분 단위, 예: 11:30 = 11:30~12:00 구간) */
    @Column(nullable = false)
    private LocalTime slotTime;

    /** 슬롯 상태 (AVAILABLE, OCCUPIED, BLOCKED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlotStatus status;

    public TimeSlotInventoryJpaEntity(Long id, Long roomOptionId, LocalDate date,
                                       LocalTime slotTime, TimeSlotStatus status) {

        this.id = id;
        this.roomOptionId = roomOptionId;
        this.date = date;
        this.slotTime = slotTime;
        this.status = status;
    }
}
