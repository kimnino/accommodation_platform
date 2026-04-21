package com.accommodation.platform.extranet.room.adapter.in.web;

import java.math.BigDecimal;
import java.util.List;

import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetDeleteRoomOptionUseCase;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetDeleteRoomUseCase;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetGetRoomQuery;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomOptionUseCase;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomUseCase;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomOptionUseCase;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomUseCase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.accommodation.platform.common.config.TestSecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.accommodation.platform.common.security.JwtTokenProvider;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(ExtranetRoomController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class ExtranetRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ExtranetRegisterRoomUseCase registerRoomUseCase;

    @MockitoBean
    private ExtranetUpdateRoomUseCase updateRoomUseCase;

    @MockitoBean
    private ExtranetDeleteRoomUseCase deleteRoomUseCase;

    @MockitoBean
    private ExtranetRegisterRoomOptionUseCase registerRoomOptionUseCase;

    @MockitoBean
    private ExtranetUpdateRoomOptionUseCase updateRoomOptionUseCase;

    @MockitoBean
    private ExtranetDeleteRoomOptionUseCase deleteRoomOptionUseCase;

    @MockitoBean
    private ExtranetGetRoomQuery getRoomQuery;

    @Test
    void 객실_등록_API() throws Exception {

        // given
        Room room = createRoom();
        given(registerRoomUseCase.register(eq(1L), eq(1L), any())).willReturn(room);

        // when & then
        mockMvc.perform(post("/api/v1/extranet/accommodations/1/rooms")
                        .header("X-Partner-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "디럭스 더블",
                                    "room_type_name": "디럭스",
                                    "standard_capacity": 2,
                                    "max_capacity": 4
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("디럭스 더블"))
                .andDo(document("extranet/room/register",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID")),
                        requestFields(
                                fieldWithPath("name").description("객실명"),
                                fieldWithPath("room_type_name").description("객실 유형명 (파트너 입력, 선택)").optional(),
                                fieldWithPath("standard_capacity").description("기준 인원"),
                                fieldWithPath("max_capacity").description("최대 인원")),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data.id").description("객실 ID"),
                                fieldWithPath("data.accommodation_id").description("숙소 ID"),
                                fieldWithPath("data.name").description("객실명"),
                                fieldWithPath("data.room_type_name").description("객실 유형명"),
                                fieldWithPath("data.standard_capacity").description("기준 인원"),
                                fieldWithPath("data.max_capacity").description("최대 인원"),
                                fieldWithPath("data.status").description("객실 상태"),
                                fieldWithPath("data.created_at").description("생성 시각"),
                                fieldWithPath("data.updated_at").description("수정 시각"),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }

    @Test
    void 객실_수정_API() throws Exception {

        // given
        Room room = createRoom();
        given(updateRoomUseCase.update(eq(1L), eq(1L), any())).willReturn(room);

        // when & then
        mockMvc.perform(put("/api/v1/extranet/accommodations/1/rooms/1")
                        .header("X-Partner-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "디럭스 트윈",
                                    "room_type_name": "디럭스",
                                    "standard_capacity": 2,
                                    "max_capacity": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("extranet/room/update",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID"))));
    }

    @Test
    void 객실_삭제_API() throws Exception {

        // when & then
        mockMvc.perform(delete("/api/v1/extranet/accommodations/1/rooms/1")
                        .header("X-Partner-Id", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("extranet/room/delete",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID"))));
    }

    @Test
    void 객실_목록_조회_API() throws Exception {

        // given
        given(getRoomQuery.getRoomsByAccommodationId(1L, 1L)).willReturn(List.of(createRoom()));

        // when & then
        mockMvc.perform(get("/api/v1/extranet/accommodations/1/rooms")
                        .header("X-Partner-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("디럭스 더블"))
                .andDo(document("extranet/room/list",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID"))));
    }

    @Test
    void 객실_옵션_등록_API() throws Exception {

        // given
        RoomOption option = createRoomOption();
        given(registerRoomOptionUseCase.register(eq(1L), eq(1L), any())).willReturn(option);

        // when & then
        mockMvc.perform(post("/api/v1/extranet/accommodations/1/rooms/1/options")
                        .header("X-Partner-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "조식 포함",
                                    "cancellation_policy": "FREE_CANCELLATION",
                                    "additional_price": 20000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("조식 포함"))
                .andDo(document("extranet/room-option/register",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID")),
                        requestFields(
                                fieldWithPath("name").description("옵션명"),
                                fieldWithPath("cancellation_policy").description("취소 정책 (FREE_CANCELLATION, NON_REFUNDABLE, PARTIAL_REFUND)"),
                                fieldWithPath("additional_price").description("추가 금액").optional()),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data.id").description("옵션 ID"),
                                fieldWithPath("data.room_id").description("객실 ID"),
                                fieldWithPath("data.name").description("옵션명"),
                                fieldWithPath("data.cancellation_policy").description("취소 정책"),
                                fieldWithPath("data.additional_price").description("추가 금액"),
                                fieldWithPath("data.created_at").description("생성 시각"),
                                fieldWithPath("data.updated_at").description("수정 시각"),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }

    @Test
    void 객실_옵션_삭제_API() throws Exception {

        // when & then
        mockMvc.perform(delete("/api/v1/extranet/accommodations/1/rooms/1/options/1")
                        .header("X-Partner-Id", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("extranet/room-option/delete",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID"))));
    }

    private Room createRoom() {

        return Room.builder()
                .accommodationId(1L)
                .name("디럭스 더블")
                .roomTypeName("디럭스")
                .standardCapacity(2)
                .maxCapacity(4)
                .build();
    }

    private RoomOption createRoomOption() {

        return RoomOption.builder()
                .roomId(1L)
                .name("조식 포함")
                .cancellationPolicy(CancellationPolicy.FREE_CANCELLATION)
                .additionalPrice(new BigDecimal("20000"))
                .build();
    }
}
