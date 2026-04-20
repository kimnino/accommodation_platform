package com.accommodation.platform.core.price.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriceDomainServiceTest {

    private final PriceDomainService service = new PriceDomainService();

    @Test
    void 박수_기반_총_가격을_합산한다() {

        // given
        List<RoomPrice> prices = List.of(
                createPrice(LocalDate.of(2026, 4, 20), new BigDecimal("100000"), true),
                createPrice(LocalDate.of(2026, 4, 21), new BigDecimal("120000"), true),
                createPrice(LocalDate.of(2026, 4, 22), new BigDecimal("80000"), true));

        // when
        BigDecimal total = service.calculateTotalPrice(prices);

        // then
        assertThat(total).isEqualByComparingTo(new BigDecimal("300000"));
    }

    @Test
    void VAT_미포함_가격에_10퍼센트를_추가한다() {

        // given
        BigDecimal price = new BigDecimal("100000");

        // when
        BigDecimal withVat = service.calculatePriceWithVat(price, false);

        // then
        assertThat(withVat).isEqualByComparingTo(new BigDecimal("110000"));
    }

    @Test
    void VAT_포함_가격은_그대로_반환한다() {

        // given
        BigDecimal price = new BigDecimal("100000");

        // when
        BigDecimal withVat = service.calculatePriceWithVat(price, true);

        // then
        assertThat(withVat).isEqualByComparingTo(new BigDecimal("100000"));
    }

    @Test
    void 날짜별_VAT_포함여부를_반영하여_총_가격을_계산한다() {

        // given
        List<RoomPrice> prices = List.of(
                createPrice(LocalDate.of(2026, 4, 20), new BigDecimal("100000"), true),
                createPrice(LocalDate.of(2026, 4, 21), new BigDecimal("100000"), false));

        // when
        BigDecimal total = service.calculateTotalPriceWithVat(prices);

        // then
        assertThat(total).isEqualByComparingTo(new BigDecimal("210000"));
    }

    private RoomPrice createPrice(LocalDate date, BigDecimal sellingPrice, boolean taxIncluded) {

        return RoomPrice.builder()
                .roomOptionId(1L)
                .date(date)
                .priceType(com.accommodation.platform.core.price.domain.enums.PriceType.STAY)
                .basePrice(sellingPrice)
                .sellingPrice(sellingPrice)
                .taxIncluded(taxIncluded)
                .build();
    }
}
