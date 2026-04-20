package com.accommodation.platform.core.supplier.domain.model;

/**
 * 내부 표준 객실 모델.
 * 공급사마다 다른 객실 데이터를 이 형태로 정규화.
 */
public record CanonicalRoom(
        String externalRoomId,
        String roomName,
        String roomTypeName,
        int standardCapacity,
        int maxCapacity
) {
}
