package com.accommodation.platform.core.supplier.domain.model;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

/**
 * 내부 표준 숙소 모델.
 * 공급사 숙소 데이터를 이 형태로 정규화.
 */
public record CanonicalAccommodation(
        String externalAccommodationId,
        String name,
        AccommodationType type,
        String fullAddress,
        double latitude,
        double longitude,
        List<CanonicalRoom> rooms
) {
}
