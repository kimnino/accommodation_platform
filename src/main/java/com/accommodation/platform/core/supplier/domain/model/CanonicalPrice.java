package com.accommodation.platform.core.supplier.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 내부 표준 가격 모델.
 * 공급사마다 다른 가격 데이터를 이 형태로 정규화.
 */
public record CanonicalPrice(
        String externalRoomId,
        LocalDate date,
        BigDecimal basePrice,
        BigDecimal sellingPrice,
        boolean taxIncluded,
        int availableQuantity
) {
}
