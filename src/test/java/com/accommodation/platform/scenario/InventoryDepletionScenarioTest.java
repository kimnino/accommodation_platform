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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 시나리오 테스트: 재고 소진 후 예약 차단
 * 첫 번째 예약 성공 → 두 번째 예약 409 차단 → remaining_quantity=0 검증
 */
@AutoConfigureMockMvc
class InventoryDepletionScenarioTest extends IntegrationTestBase {

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

    private static final LocalDate CHECK_IN_DATE = LocalDate.of(2026, 5, 10);
    private static final LocalDate CHECK_OUT_DATE = LocalDate.of(2026, 5, 11);
    private static final Long MEMBER_ID = 3L;

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
                        null, 1L, "재고 소진 테스트 호텔", AccommodationType.HOTEL,
                        null, "서울시 중구 명동 789",
                        37.5636, 126.9869, "명동역 3번 출구",
                        AccommodationStatus.ACTIVE,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        accommodationId = accommodation.getId();

        // given: 객실 저장
        RoomJpaEntity room = roomJpaRepository.save(
                new RoomJpaEntity(null, accommodationId, "싱글 룸", "싱글", 1, 2, RoomStatus.ACTIVE)
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

        // given: 재고 remaining_quantity=1 (마지막 재고)
        inventoryJpaRepository.save(
                new InventoryJpaEntity(null, roomOptionId, CHECK_IN_DATE, 1, 1, InventoryStatus.AVAILABLE)
        );

        // given: STAY 가격 저장
        roomPriceJpaRepository.save(
                new RoomPriceJpaEntity(
                        null, roomOptionId, CHECK_IN_DATE,
                        PriceType.STAY,
                        new BigDecimal("80000"), new BigDecimal("70000"), true
                )
        );
    }

    @Test
    void 재고_소진_후_예약_차단_시나리오() throws Exception {

        // Step 1: 첫 번째 예약 → 201 성공
        // when
        mockMvc.perform(post("/api/v1/reservations/stay")
                        .header("X-Member-Id", MEMBER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservation_request_id": "depletion-req-001",
                                  "accommodation_id": %d,
                                  "room_option_id": %d,
                                  "check_in_date": "%s",
                                  "check_out_date": "%s",
                                  "guest_name": "첫번째투숙객",
                                  "guest_phone": "010-1111-2222",
                                  "guest_email": "first@example.com"
                                }
                                """.formatted(accommodationId, roomOptionId, CHECK_IN_DATE, CHECK_OUT_DATE)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // Step 2: 두 번째 예약 (동일 옵션/날짜, 다른 reservation_request_id) → 409 INVENTORY_NOT_AVAILABLE
        // when
        mockMvc.perform(post("/api/v1/reservations/stay")
                        .header("X-Member-Id", MEMBER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservation_request_id": "depletion-req-002",
                                  "accommodation_id": %d,
                                  "room_option_id": %d,
                                  "check_in_date": "%s",
                                  "check_out_date": "%s",
                                  "guest_name": "두번째투숙객",
                                  "guest_phone": "010-3333-4444",
                                  "guest_email": "second@example.com"
                                }
                                """.formatted(accommodationId, roomOptionId, CHECK_IN_DATE, CHECK_OUT_DATE)))
                // then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("INVENTORY_NOT_AVAILABLE"));

        // Step 3: DB에서 remaining_quantity=0 확인
        // then
        InventoryJpaEntity inventory = inventoryJpaRepository
                .findByRoomOptionIdAndDateBetweenOrderByDateAsc(roomOptionId, CHECK_IN_DATE, CHECK_IN_DATE)
                .getFirst();
        assertThat(inventory.getRemainingQuantity()).isZero();
    }
}
