package com.accommodation.platform.customer.reservation.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;
import com.accommodation.platform.core.reservation.domain.model.GuestInfo;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCancelReservationUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerConfirmPaymentUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerGetReservationQuery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerReservationController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class CustomerReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerCreateReservationUseCase createUseCase;

    @MockitoBean
    private CustomerCancelReservationUseCase cancelUseCase;

    @MockitoBean
    private CustomerConfirmPaymentUseCase confirmPaymentUseCase;

    @MockitoBean
    private CustomerGetReservationQuery getQuery;

    @Test
    void 숙박_예약_생성_API() throws Exception {

        // given
        Reservation reservation = buildReservation(ReservationType.STAY);
        given(createUseCase.createStayReservation(any())).willReturn(reservation);

        // when & then
        mockMvc.perform(post("/api/v1/reservations/stay")
                        .header("X-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservation_request_id": "req-001",
                                  "accommodation_id": 100,
                                  "room_option_id": 10,
                                  "check_in_date": "2026-06-01",
                                  "check_out_date": "2026-06-03",
                                  "guest_name": "홍길동",
                                  "guest_phone": "010-1234-5678",
                                  "guest_email": "hong@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(document("customer-reservation-stay",
                        requestHeaders(
                                headerWithName("X-Member-Id").description("회원 ID")),
                        requestFields(
                                fieldWithPath("reservation_request_id").description("예약 요청 ID (멱등성 키)"),
                                fieldWithPath("accommodation_id").description("숙소 ID"),
                                fieldWithPath("room_option_id").description("객실 옵션 ID"),
                                fieldWithPath("check_in_date").description("체크인 날짜"),
                                fieldWithPath("check_out_date").description("체크아웃 날짜"),
                                fieldWithPath("guest_name").description("투숙객 이름"),
                                fieldWithPath("guest_phone").description("투숙객 연락처"),
                                fieldWithPath("guest_email").description("투숙객 이메일 (선택)").optional()),
                        reservationResponseFields()));
    }

    @Test
    void 결제_확정_API() throws Exception {

        // given
        Reservation reservation = buildReservation(ReservationType.STAY);
        given(confirmPaymentUseCase.confirmPayment(eq(1L), eq(1L))).willReturn(reservation);

        // when & then
        mockMvc.perform(post("/api/v1/reservations/1/confirm-payment")
                        .header("X-Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(document("customer-reservation-confirm-payment",
                        requestHeaders(
                                headerWithName("X-Member-Id").description("회원 ID")),
                        reservationResponseFields()));
    }

    @Test
    void 예약_취소_API() throws Exception {

        // given
        willDoNothing().given(cancelUseCase).cancel(eq(1L), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/v1/reservations/1")
                        .header("X-Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(document("customer-reservation-cancel",
                        requestHeaders(
                                headerWithName("X-Member-Id").description("회원 ID")),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data").description("데이터 (null)").optional(),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }

    @Test
    void 예약_단건_조회_API() throws Exception {

        // given
        Reservation reservation = buildReservation(ReservationType.STAY);
        given(getQuery.getById(eq(1L), eq(1L))).willReturn(reservation);

        // when & then
        mockMvc.perform(get("/api/v1/reservations/1")
                        .header("X-Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(document("customer-reservation-get",
                        requestHeaders(
                                headerWithName("X-Member-Id").description("회원 ID")),
                        reservationResponseFields()));
    }

    private Reservation buildReservation(ReservationType type) {

        return Reservation.builder()
                .id(1L)
                .memberId(1L)
                .accommodationId(100L)
                .roomOptionId(10L)
                .reservationType(type)
                .checkInDate(LocalDate.of(2026, 6, 1))
                .checkOutDate(LocalDate.of(2026, 6, 3))
                .guestInfo(new GuestInfo("홍길동", "010-1234-5678", "hong@example.com"))
                .totalPrice(new BigDecimal("200000"))
                .build();
    }

    private org.springframework.restdocs.payload.ResponseFieldsSnippet reservationResponseFields() {

        return responseFields(
                fieldWithPath("status").description("응답 상태"),
                fieldWithPath("data.id").description("예약 ID").optional(),
                fieldWithPath("data.reservation_number").description("예약 번호"),
                fieldWithPath("data.reservation_type").description("예약 유형 (STAY, HOURLY)"),
                fieldWithPath("data.accommodation_id").description("숙소 ID"),
                fieldWithPath("data.room_option_id").description("객실 옵션 ID"),
                fieldWithPath("data.check_in_date").description("체크인 날짜").optional(),
                fieldWithPath("data.check_out_date").description("체크아웃 날짜").optional(),
                fieldWithPath("data.hourly_start_time").description("대실 시작 시간").optional(),
                fieldWithPath("data.guest_name").description("투숙객 이름"),
                fieldWithPath("data.total_price").description("총 결제 금액"),
                fieldWithPath("data.status").description("예약 상태"),
                fieldWithPath("data.hold_expired_at").description("결제 대기 만료 시각").optional(),
                fieldWithPath("data.created_at").description("예약 생성 시각").optional(),
                fieldWithPath("error").description("에러 정보").optional(),
                fieldWithPath("timestamp").description("응답 시각"));
    }
}
