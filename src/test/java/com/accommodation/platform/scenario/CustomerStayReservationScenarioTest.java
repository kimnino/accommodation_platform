package com.accommodation.platform.scenario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationJpaEntity;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationJpaRepository;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaEntity;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaRepository;
import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;
import com.accommodation.platform.core.price.adapter.out.persistence.RoomPriceJpaEntity;
import com.accommodation.platform.core.price.adapter.out.persistence.RoomPriceJpaRepository;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.reservation.adapter.out.persistence.ReservationJpaRepository;
import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomJpaRepository;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionJpaRepository;
import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;
import com.accommodation.platform.core.room.domain.enums.RoomStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 시나리오 테스트: 숙박 예약 전체 플로우
 * 숙소 검색 → 상세 조회 → 예약 생성 → 결제 확정 → 재고 차감 검증
 */
@AutoConfigureMockMvc
class CustomerStayReservationScenarioTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccommodationJpaRepository accommodationJpaRepository;

    @Autowired
    private RoomJpaRepository roomJpaRepository;

    @Autowired
    private RoomOptionJpaRepository roomOptionJpaRepository;

    @Autowired
    private InventoryJpaRepository inventoryJpaRepository;

    @Autowired
    private RoomPriceJpaRepository roomPriceJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    private static final LocalDate CHECK_IN_DATE = LocalDate.of(2026, 5, 1);
    private static final LocalDate CHECK_OUT_DATE = LocalDate.of(2026, 5, 2);
    private static final Long MEMBER_ID = 1L;

    private Long accommodationId;
    private Long roomOptionId;

    @BeforeEach
    void setUp() {
        // 데이터 초기화
        reservationJpaRepository.deleteAll();
        inventoryJpaRepository.deleteAll();
        roomPriceJpaRepository.deleteAll();
        roomOptionJpaRepository.deleteAll();
        roomJpaRepository.deleteAll();
        accommodationJpaRepository.deleteAll();

        // given: 숙소 저장
        AccommodationJpaEntity accommodation = accommodationJpaRepository.save(
                new AccommodationJpaEntity(
                        null, 1L, "테스트 호텔", AccommodationType.HOTEL,
                        null, "서울시 강남구 테헤란로 123",
                        37.5665, 126.9780, "강남역 5번 출구",
                        AccommodationStatus.ACTIVE,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        accommodationId = accommodation.getId();

        // given: 객실 저장
        RoomJpaEntity room = roomJpaRepository.save(
                new RoomJpaEntity(null, accommodationId, "디럭스 더블", "더블", 2, 3, RoomStatus.ACTIVE)
        );

        // given: 객실 옵션 저장
        RoomOptionJpaEntity roomOption = roomOptionJpaRepository.save(
                new RoomOptionJpaEntity(
                        null, room.getId(), "기본",
                        CancellationPolicy.FREE_CANCELLATION,
                        BigDecimal.ZERO, null, null,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        roomOptionId = roomOption.getId();

        // given: 재고 저장 (remaining=2)
        inventoryJpaRepository.save(
                new InventoryJpaEntity(null, roomOptionId, CHECK_IN_DATE, 2, 2, InventoryStatus.AVAILABLE)
        );

        // given: STAY 가격 저장
        roomPriceJpaRepository.save(
                new RoomPriceJpaEntity(
                        null, roomOptionId, CHECK_IN_DATE,
                        PriceType.STAY,
                        new BigDecimal("150000"), new BigDecimal("120000"), true
                )
        );
    }

    @Test
    void 숙박_예약_전체_플로우_시나리오() throws Exception {

        // Step 1: 숙소 검색
        // when
        mockMvc.perform(get("/api/v1/accommodations")
                        .param("check_in_date", CHECK_IN_DATE.toString())
                        .param("check_out_date", CHECK_OUT_DATE.toString()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").isArray());

        // Step 2: 숙소 상세 조회
        // when
        mockMvc.perform(get("/api/v1/accommodations/{id}", accommodationId)
                        .param("check_in_date", CHECK_IN_DATE.toString())
                        .param("check_out_date", CHECK_OUT_DATE.toString()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("테스트 호텔"));

        // Step 3: 숙박 예약 생성
        // when
        String createResponse = mockMvc.perform(post("/api/v1/reservations/stay")
                        .header("X-Member-Id", MEMBER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservation_request_id": "stay-scenario-001",
                                  "accommodation_id": %d,
                                  "room_option_id": %d,
                                  "check_in_date": "%s",
                                  "check_out_date": "%s",
                                  "guest_name": "홍길동",
                                  "guest_phone": "010-1234-5678",
                                  "guest_email": "hong@example.com"
                                }
                                """.formatted(accommodationId, roomOptionId, CHECK_IN_DATE, CHECK_OUT_DATE)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value(ReservationStatus.PAYMENT_WAITING.name()))
                .andReturn().getResponse().getContentAsString();

        // 예약 ID 추출
        Long reservationId = com.jayway.jsonpath.JsonPath.parse(createResponse).read("$.data.id", Long.class);
        assertThat(reservationId).isNotNull();

        // Step 4: 결제 확정
        // when
        mockMvc.perform(post("/api/v1/reservations/{id}/confirm-payment", reservationId)
                        .header("X-Member-Id", MEMBER_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value(ReservationStatus.CONFIRMED.name()));

        // Step 5: 재고 remaining_quantity 1 감소 검증
        // then
        InventoryJpaEntity updatedInventory = inventoryJpaRepository
                .findByRoomOptionIdAndDateBetweenOrderByDateAsc(roomOptionId, CHECK_IN_DATE, CHECK_IN_DATE)
                .getFirst();
        assertThat(updatedInventory.getRemainingQuantity()).isEqualTo(1);
    }
}
