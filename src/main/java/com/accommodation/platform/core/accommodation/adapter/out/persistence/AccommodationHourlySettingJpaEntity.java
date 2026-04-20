package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.time.LocalTime;

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
 * 숙소별 대실(시간제) 운영 설정 테이블.
 * 대실을 운영하는 숙소만 데이터가 존재.
 * 슬롯 시간 단위(30분)는 시스템 상수로 고정.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_hourly_setting")
public class AccommodationHourlySettingJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 숙소 ID */
    @Column(nullable = false, unique = true)
    private Long accommodationId;

    /** 대실 운영 시작 시간 (예: 10:00) */
    @Column(nullable = false)
    private LocalTime operatingStartTime;

    /** 대실 운영 종료 시간 (예: 22:00). 이 시간 이후에는 대실 시작 불가 */
    @Column(nullable = false)
    private LocalTime operatingEndTime;

    /** 1회 이용 시간 (분 단위, 예: 240 = 4시간) */
    private int usageDurationMinutes;

    /** 청소/정비 시간 (분 단위, 예: 30) */
    private int bufferMinutes;

    /** 슬롯 단위 (30 또는 60분). 파트너가 선택 */
    private int slotUnitMinutes;

    public AccommodationHourlySettingJpaEntity(Long accommodationId,
                                                LocalTime operatingStartTime, LocalTime operatingEndTime,
                                                int usageDurationMinutes, int bufferMinutes,
                                                int slotUnitMinutes) {

        this.accommodationId = accommodationId;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.usageDurationMinutes = usageDurationMinutes;
        this.bufferMinutes = bufferMinutes;
        this.slotUnitMinutes = slotUnitMinutes;
    }
}
