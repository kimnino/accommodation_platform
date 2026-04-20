package com.accommodation.platform.core.supplier.adapter.out.external;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.supplier.application.port.out.SupplierClient;
import com.accommodation.platform.core.supplier.domain.model.CanonicalAccommodation;
import com.accommodation.platform.core.supplier.domain.model.CanonicalPrice;
import com.accommodation.platform.core.supplier.domain.model.CanonicalRoom;

import lombok.extern.slf4j.Slf4j;

/**
 * MINHYUK_HOUSE 공급사 API 어댑터.
 * 실제 API 호출 대신 가상 데이터를 반환.
 * 실제 연동 시 RestClient로 교체하며 Virtual Threads 활용.
 */
@Slf4j
@Component
public class MinhyukHouseSupplierAdapter implements SupplierClient {

    @Override
    public String getSupplierCode() {

        return "MINHYUK_HOUSE";
    }

    @Override
    public List<CanonicalAccommodation> fetchAccommodations() {

        log.info("[MINHYUK_HOUSE] 숙소 목록 조회 요청");

        // 가상 응답 — 실제 API 호출 시 RestClient로 교체
        return List.of(
                new CanonicalAccommodation(
                        "MH-ACC-001",
                        "민혁하우스 강남점",
                        "HOTEL",
                        "서울시 강남구 역삼동 123-45",
                        37.4979,
                        127.0276,
                        List.of(
                                new CanonicalRoom("MH-ROOM-001", "Standard Double", "스탠다드", 2, 3),
                                new CanonicalRoom("MH-ROOM-002", "Deluxe Twin", "디럭스", 2, 4),
                                new CanonicalRoom("MH-ROOM-003", "Suite", "스위트", 2, 5))),
                new CanonicalAccommodation(
                        "MH-ACC-002",
                        "민혁하우스 홍대점",
                        "GUEST_HOUSE",
                        "서울시 마포구 서교동 456-78",
                        37.5563,
                        126.9236,
                        List.of(
                                new CanonicalRoom("MH-ROOM-004", "Dormitory 4-Bed", "도미토리", 1, 4),
                                new CanonicalRoom("MH-ROOM-005", "Private Single", "개인실", 1, 2))));
    }

    @Override
    public List<CanonicalPrice> fetchPrices(String externalAccommodationId,
                                             LocalDate startDate, LocalDate endDate) {

        log.info("[MINHYUK_HOUSE] 가격/재고 조회: accommodation={}, {} ~ {}",
                externalAccommodationId, startDate, endDate);

        List<CanonicalPrice> prices = new ArrayList<>();

        List<String> roomIds = switch (externalAccommodationId) {
            case "MH-ACC-001" -> List.of("MH-ROOM-001", "MH-ROOM-002", "MH-ROOM-003");
            case "MH-ACC-002" -> List.of("MH-ROOM-004", "MH-ROOM-005");
            default -> List.of();
        };

        for (String roomId : roomIds) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                BigDecimal basePrice = getBasePrice(roomId, date);
                prices.add(new CanonicalPrice(
                        roomId, date, basePrice, basePrice, true, 5));
            }
        }

        return prices;
    }

    private BigDecimal getBasePrice(String roomId, LocalDate date) {

        boolean isWeekend = date.getDayOfWeek().getValue() >= 6;
        BigDecimal weekdayPrice = switch (roomId) {
            case "MH-ROOM-001" -> new BigDecimal("80000");
            case "MH-ROOM-002" -> new BigDecimal("120000");
            case "MH-ROOM-003" -> new BigDecimal("200000");
            case "MH-ROOM-004" -> new BigDecimal("25000");
            case "MH-ROOM-005" -> new BigDecimal("50000");
            default -> new BigDecimal("100000");
        };

        return isWeekend
                ? weekdayPrice.multiply(new BigDecimal("1.3")).setScale(0, java.math.RoundingMode.HALF_UP)
                : weekdayPrice;
    }
}
