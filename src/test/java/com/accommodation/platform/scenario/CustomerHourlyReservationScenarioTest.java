package com.accommodation.platform.scenario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationJpaEntity;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationJpaRepository;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.inventory.adapter.out.persistence.TimeSlotInventoryJpaEntity;
import com.accommodation.platform.core.inventory.adapter.out.persistence.TimeSlotInventoryJpaRepository;
import com.accommodation.platform.core.inventory.domain.enums.TimeSlotStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 시나리오 테스트: 대실 예약 전체 플로우
 * 대실 예약 생성 → 결제 확정 → 타임슬롯 OCCUPIED 변경 검증
 */
@AutoConfigureMockMvc
class CustomerHourlyReservationScenarioTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccommodationJpaRepository accommodationJpaRepository;

    @Autowired
    private RoomJpaRepository roomJpaRepository;

    @Autowired
    private RoomOptionJpaRepository roomOptionJpaRepository;

    @Autowired
    private TimeSlotInventoryJpaRepository timeSlotInventoryJpaRepository;

    @Autowired
    private RoomPriceJpaRepository roomPriceJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    private static final LocalDate USE_DATE = LocalDate.of(2026, 5, 1);
    private static final LocalTime START_TIME = LocalTime.of(14, 0);
    private static final LocalTime END_TIME = LocalTime.of(18, 0);
    private static final Long MEMBER_ID = 2L;

    private Long accommodationId;
    private Long roomOptionId;

    @BeforeEach
    void setUp() {
        // 데이터 초기화
        reservationJpaRepository.deleteAll();
        timeSlotInventoryJpaRepository.deleteAll();
        roomPriceJpaRepository.deleteAll();
        roomOptionJpaRepository.deleteAll();
        roomJpaRepository.deleteAll();
        accommodationJpaRepository.deleteAll();

        // given: MOTEL 타입 숙소 저장
        AccommodationJpaEntity accommodation = accommodationJpaRepository.save(
                new AccommodationJpaEntity(
                        null, 1L, "테스트 모텔", AccommodationType.MOTEL,
                        null, "서울시 마포구 홍대입구 456",
                        37.5563, 126.9236, "홍대입구역 2번 출구",
                        AccommodationStatus.ACTIVE,
                        LocalTime.of(10, 0), LocalTime.of(22, 0)
                )
        );
        accommodationId = accommodation.getId();

        // given: 객실 저장
        RoomJpaEntity room = roomJpaRepository.save(
                new RoomJpaEntity(null, accommodationId, "스탠다드 룸", "스탠다드", 2, 2, RoomStatus.ACTIVE)
        );

        // given: 대실 옵션 저장 (10:00~22:00 운영)
        RoomOptionJpaEntity roomOption = roomOptionJpaRepository.save(
                new RoomOptionJpaEntity(
                        null, room.getId(), "대실 기본",
                        CancellationPolicy.NON_REFUNDABLE,
                        BigDecimal.ZERO,
                        LocalTime.of(10, 0), LocalTime.of(22, 0),
                        null, null
                )
        );
        roomOptionId = roomOption.getId();

        // given: 10:00~22:00 30분 단위 타임슬롯 AVAILABLE로 저장
        List<TimeSlotInventoryJpaEntity> slots = new ArrayList<>();
        LocalTime slot = LocalTime.of(10, 0);
        while (slot.isBefore(LocalTime.of(22, 0))) {
            slots.add(new TimeSlotInventoryJpaEntity(null, roomOptionId, USE_DATE, slot, TimeSlotStatus.AVAILABLE));
            slot = slot.plusMinutes(30);
        }
        timeSlotInventoryJpaRepository.saveAll(slots);

        // given: HOURLY 가격 저장
        roomPriceJpaRepository.save(
                new RoomPriceJpaEntity(
                        null, roomOptionId, USE_DATE,
                        PriceType.HOURLY,
                        new BigDecimal("30000"), new BigDecimal("25000"), true
                )
        );
    }

    @Test
    void 대실_예약_전체_플로우_시나리오() throws Exception {

        // Step 1: 대실 예약 생성 (14:00~18:00)
        // when
        String createResponse = mockMvc.perform(post("/api/v1/reservations/hourly")
                        .header("X-Member-Id", MEMBER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservation_request_id": "hourly-scenario-001",
                                  "accommodation_id": %d,
                                  "room_option_id": %d,
                                  "date": "%s",
                                  "start_time": "%s",
                                  "end_time": "%s",
                                  "guest_name": "김대실",
                                  "guest_phone": "010-9876-5432",
                                  "guest_email": "hourly@example.com"
                                }
                                """.formatted(accommodationId, roomOptionId, USE_DATE, START_TIME, END_TIME)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value(ReservationStatus.PAYMENT_WAITING.name()))
                .andReturn().getResponse().getContentAsString();

        // 예약 ID 추출
        Long reservationId = com.jayway.jsonpath.JsonPath.parse(createResponse).read("$.data.id", Long.class);
        assertThat(reservationId).isNotNull();

        // Step 2: 결제 확정
        // when
        mockMvc.perform(post("/api/v1/reservations/{id}/confirm-payment", reservationId)
                        .header("X-Member-Id", MEMBER_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value(ReservationStatus.CONFIRMED.name()));

        // Step 3: 14:00~17:30 슬롯이 OCCUPIED로 변경됐는지 검증
        // then: 예약 구간(14:00~18:00) 내 슬롯들은 OCCUPIED 또는 BLOCKED여야 함
        List<TimeSlotInventoryJpaEntity> occupiedSlots = timeSlotInventoryJpaRepository
                .findByRoomOptionIdAndDateAndSlotTimeBetweenOrderBySlotTimeAsc(
                        roomOptionId, USE_DATE,
                        LocalTime.of(14, 0), LocalTime.of(17, 30));

        assertThat(occupiedSlots).isNotEmpty();
        assertThat(occupiedSlots).allMatch(slot ->
                slot.getStatus() == TimeSlotStatus.OCCUPIED || slot.getStatus() == TimeSlotStatus.BLOCKED
        );

        // 예약 구간 외 슬롯(10:00~13:30)은 AVAILABLE 상태 유지
        List<TimeSlotInventoryJpaEntity> availableSlots = timeSlotInventoryJpaRepository
                .findByRoomOptionIdAndDateAndSlotTimeBetweenOrderBySlotTimeAsc(
                        roomOptionId, USE_DATE,
                        LocalTime.of(10, 0), LocalTime.of(13, 30));

        assertThat(availableSlots).isNotEmpty();
        assertThat(availableSlots).allMatch(slot -> slot.getStatus() == TimeSlotStatus.AVAILABLE);
    }
}
