package com.accommodation.platform.core.price.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

@Service
public class PriceDomainService {

    private static final BigDecimal KOREA_VAT_RATE = new BigDecimal("0.10");

    /**
     * 박수 기반 총 가격 합산.
     * 날짜별 판매가를 합산하여 총 가격 반환.
     */
    public BigDecimal calculateTotalPrice(List<RoomPrice> prices) {

        return prices.stream()
                .map(RoomPrice::getSellingPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * VAT 포함 가격 계산.
     * taxIncluded=false인 경우 VAT를 추가.
     */
    public BigDecimal calculatePriceWithVat(BigDecimal price, boolean taxIncluded) {

        if (taxIncluded) {
            return price;
        }
        BigDecimal vat = price.multiply(KOREA_VAT_RATE).setScale(0, RoundingMode.HALF_UP);
        return price.add(vat);
    }

    /**
     * 총 가격에 VAT 적용.
     * 각 날짜별 taxIncluded 여부를 반영하여 최종 가격 계산.
     */
    public BigDecimal calculateTotalPriceWithVat(List<RoomPrice> prices) {

        return prices.stream()
                .map(price -> calculatePriceWithVat(price.getSellingPrice(), price.isTaxIncluded()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
