package com.accommodation.platform.core.inventory.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.inventory.domain.enums.TimeSlotStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * 30분, 60분 단위 타임 블록.
 * slotTime = 11:30이면 11:30~12:00 구간을 의미.
 * 대실 예약 시 연속 블록을 점유(OCCUPIED) + 버퍼 블록을 차단(BLOCKED).
 */
@Getter
public class TimeSlotInventory extends BaseEntity {

    private Long id;
    private Long roomOptionId;
    private LocalDate date;
    private LocalTime slotTime;
    private TimeSlotStatus status;

    @Builder
    public TimeSlotInventory(Long id, Long roomOptionId, LocalDate date,
                              LocalTime slotTime, TimeSlotStatus status) {

        validateRequired(roomOptionId, date, slotTime);
        this.id = id;
        this.roomOptionId = roomOptionId;
        this.date = date;
        this.slotTime = slotTime;
        this.status = status != null ? status : TimeSlotStatus.AVAILABLE;
        initTimestamps();
    }

    public void occupy() {

        if (this.status != TimeSlotStatus.AVAILABLE) {
            throw new IllegalStateException("이미 사용 중인 슬롯입니다: " + slotTime);
        }
        this.status = TimeSlotStatus.OCCUPIED;
        updateTimestamp();
    }

    public void block() {

        if (this.status != TimeSlotStatus.AVAILABLE) {
            throw new IllegalStateException("이미 사용 중인 슬롯입니다: " + slotTime);
        }
        this.status = TimeSlotStatus.BLOCKED;
        updateTimestamp();
    }

    public void release() {

        this.status = TimeSlotStatus.AVAILABLE;
        updateTimestamp();
    }

    public boolean isAvailable() {

        return this.status == TimeSlotStatus.AVAILABLE;
    }

    private void validateRequired(Long roomOptionId, LocalDate date, LocalTime slotTime) {

        if (roomOptionId == null) {
            throw new IllegalArgumentException("roomOptionId는 필수입니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }
        if (slotTime == null) {
            throw new IllegalArgumentException("슬롯 시간은 필수입니다.");
        }
    }
}
