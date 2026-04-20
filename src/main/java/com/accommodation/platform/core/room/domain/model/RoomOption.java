package com.accommodation.platform.core.room.domain.model;

import java.math.BigDecimal;

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

    @Builder
    public RoomOption(Long id, Long roomId, String name,
                      CancellationPolicy cancellationPolicy, BigDecimal additionalPrice) {

        validateRequired(roomId, name, cancellationPolicy);
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.cancellationPolicy = cancellationPolicy;
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        initTimestamps();
    }

    public void updateInfo(String name, CancellationPolicy cancellationPolicy, BigDecimal additionalPrice) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (cancellationPolicy != null) {
            this.cancellationPolicy = cancellationPolicy;
        }
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
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
