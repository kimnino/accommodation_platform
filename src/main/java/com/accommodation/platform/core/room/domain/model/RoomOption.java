package com.accommodation.platform.core.room.domain.model;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;

@Getter
public class RoomOption extends BaseEntity {

    private Long id;
    private final Long roomId;
    private String name;
    private CancellationPolicy cancellationPolicy;
    private BigDecimal additionalPrice;
    /** 대실 시작 시간 (nullable) */
    private LocalTime hourlyStartTime;
    /** 대실 종료 시간 (nullable) */
    private LocalTime hourlyEndTime;
    /** 숙박 체크인 시간 — null이면 숙소 기본값 사용 (레이트 체크인 등 옵션별 재정의용) */
    private LocalTime checkInTime;
    /** 숙박 체크아웃 시간 — null이면 숙소 기본값 사용 (레이트 체크아웃 등 옵션별 재정의용) */
    private LocalTime checkOutTime;

    @Builder
    public RoomOption(Long id, Long roomId, String name,
                      CancellationPolicy cancellationPolicy, BigDecimal additionalPrice,
                      LocalTime hourlyStartTime, LocalTime hourlyEndTime,
                      LocalTime checkInTime, LocalTime checkOutTime) {

        validateRequired(roomId, name, cancellationPolicy);
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.cancellationPolicy = cancellationPolicy;
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        this.hourlyStartTime = hourlyStartTime;
        this.hourlyEndTime = hourlyEndTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        initTimestamps();
    }

    public void updateInfo(String name, CancellationPolicy cancellationPolicy, BigDecimal additionalPrice,
                           LocalTime hourlyStartTime, LocalTime hourlyEndTime,
                           LocalTime checkInTime, LocalTime checkOutTime) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (cancellationPolicy != null) {
            this.cancellationPolicy = cancellationPolicy;
        }
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        this.hourlyStartTime = hourlyStartTime;
        this.hourlyEndTime = hourlyEndTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        updateTimestamp();
    }

    void setId(Long id) {

        this.id = id;
    }

    private void validateRequired(Long roomId, String name, CancellationPolicy cancellationPolicy) {

        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("옵션명은 필수입니다.");
        }
        if (cancellationPolicy == null) {
            throw new IllegalArgumentException("취소 정책은 필수입니다.");
        }
    }
}
