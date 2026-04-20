package com.accommodation.platform.core.price.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.price.domain.enums.PriceType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomPrice extends BaseEntity {

    private Long id;
    private Long roomOptionId;
    private LocalDate date;
    private PriceType priceType;
    private BigDecimal basePrice;
    private BigDecimal sellingPrice;
    private boolean taxIncluded;

    @Builder
    public RoomPrice(Long id, Long roomOptionId, LocalDate date, PriceType priceType,
                     BigDecimal basePrice, BigDecimal sellingPrice, boolean taxIncluded) {

        validateRequired(roomOptionId, date, priceType, basePrice, sellingPrice);
        this.id = id;
        this.roomOptionId = roomOptionId;
        this.date = date;
        this.priceType = priceType;
        this.basePrice = basePrice;
        this.sellingPrice = sellingPrice;
        this.taxIncluded = taxIncluded;
        initTimestamps();
    }

    public void updatePrice(BigDecimal basePrice, BigDecimal sellingPrice, boolean taxIncluded) {

        validatePrice(basePrice, sellingPrice);
        this.basePrice = basePrice;
        this.sellingPrice = sellingPrice;
        this.taxIncluded = taxIncluded;
        updateTimestamp();
    }

    private void validateRequired(Long roomOptionId, LocalDate date, PriceType priceType,
                                   BigDecimal basePrice, BigDecimal sellingPrice) {

        if (roomOptionId == null) {
            throw new IllegalArgumentException("roomOptionId는 필수입니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }
        if (priceType == null) {
            throw new IllegalArgumentException("가격 유형은 필수입니다.");
        }
        validatePrice(basePrice, sellingPrice);
    }

    private void validatePrice(BigDecimal basePrice, BigDecimal sellingPrice) {

        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("기본 가격은 0 이상이어야 합니다.");
        }
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("판매 가격은 0 이상이어야 합니다.");
        }
    }
}
