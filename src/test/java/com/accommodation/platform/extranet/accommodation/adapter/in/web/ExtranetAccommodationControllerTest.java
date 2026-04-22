package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import java.time.LocalTime;
import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetGetAccommodationQuery;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetUpdateAccommodationUseCase;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(ExtranetAccommodationController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class ExtranetAccommodationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ExtranetRegisterAccommodationUseCase registerUseCase;

    @MockitoBean
    private ExtranetUpdateAccommodationUseCase updateUseCase;

    @MockitoBean
    private ExtranetGetAccommodationQuery getQuery;

    @Test
    void 숙소_등록_API() throws Exception {

        // given
        Accommodation accommodation = Accommodation.builder()
                .partnerId(1L)
                .name("서울 호텔")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구 테헤란로 123")
                .latitude(37.5665)
                .longitude(126.9780)
                .locationDescription("강남역 5번 출구 도보 3분")
                .checkInTime(LocalTime.of(15, 0))
                .checkOutTime(LocalTime.of(11, 0))
                .build();

        given(registerUseCase.register(any())).willReturn(accommodation);

        // when & then
        mockMvc.perform(post("/api/v1/extranet/accommodations")
                        .header("X-Partner-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "서울 호텔",
                                    "type": "HOTEL",
                                    "full_address": "서울시 강남구 테헤란로 123",
                                    "latitude": 37.5665,
                                    "longitude": 126.9780,
                                    "location_description": "강남역 5번 출구 도보 3분",
                                    "check_in_time": "15:00",
                                    "check_out_time": "11:00",
                                    "supported_locales": ["KO", "EN"],
                                    "translations": [
                                        {
                                            "locale": "ko",
                                            "name": "서울 호텔",
                                            "full_address": "서울시 강남구 테헤란로 123",
                                            "introduction": "강남 최고의 호텔"
                                        },
                                        {
                                            "locale": "en",
                                            "name": "Seoul Hotel",
                                            "full_address": "123 Teheran-ro, Gangnam-gu, Seoul",
                                            "introduction": "The best hotel in Gangnam"
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("서울 호텔"))
                .andExpect(jsonPath("$.data.type").value("HOTEL"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andDo(document("extranet/accommodation/register",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID")),
                        requestFields(
                                fieldWithPath("name").description("숙소명"),
                                fieldWithPath("type").description("숙소 유형 (HOTEL, RESORT, PENSION 등)"),
                                fieldWithPath("full_address").description("풀주소 (도로명/지번)"),
                                fieldWithPath("latitude").description("위도"),
                                fieldWithPath("longitude").description("경도"),
                                fieldWithPath("location_description").description("위치 보충 설명").optional(),
                                fieldWithPath("check_in_time").description("체크인 시간 (HH:mm)").optional(),
                                fieldWithPath("check_out_time").description("체크아웃 시간 (HH:mm)").optional(),
                                fieldWithPath("supported_locales").description("지원 언어 목록 (KO, EN, JA)").optional(),
                                fieldWithPath("translations").description("다국어 번역 데이터").optional(),
                                fieldWithPath("translations[].locale").description("언어 코드 (ko, en, ja)"),
                                fieldWithPath("translations[].name").description("숙소명 (해당 언어)").optional(),
                                fieldWithPath("translations[].full_address").description("주소 (해당 언어)").optional(),
                                fieldWithPath("translations[].location_description").description("위치 설명 (해당 언어)").optional().type("String"),
                                fieldWithPath("translations[].introduction").description("숙소 소개 (해당 언어)").optional(),
                                fieldWithPath("translations[].service_and_facilities").description("서비스 및 부대시설 (해당 언어)").optional().type("String"),
                                fieldWithPath("translations[].usage_info").description("이용 정보 (해당 언어)").optional().type("String"),
                                fieldWithPath("translations[].cancellation_and_refund_policy").description("취소/환불 규정 (해당 언어)").optional().type("String")),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data.id").description("숙소 ID"),
                                fieldWithPath("data.name").description("숙소명"),
                                fieldWithPath("data.type").description("숙소 유형"),
                                fieldWithPath("data.status").description("숙소 상태"),
                                fieldWithPath("data.region_id").description("지역 ID").optional(),
                                fieldWithPath("data.full_address").description("풀주소"),
                                fieldWithPath("data.latitude").description("위도"),
                                fieldWithPath("data.longitude").description("경도"),
                                fieldWithPath("data.location_description").description("위치 설명"),
                                fieldWithPath("data.check_in_time").description("체크인 시간"),
                                fieldWithPath("data.check_out_time").description("체크아웃 시간"),
                                fieldWithPath("data.images").description("이미지 목록"),
                                fieldWithPath("data.created_at").description("생성 시각"),
                                fieldWithPath("data.updated_at").description("수정 시각"),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }

    @Test
    void 내_숙소_목록_조회_API() throws Exception {

        // given
        Accommodation accommodation = Accommodation.builder()
                .partnerId(1L)
                .name("서울 호텔")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        given(getQuery.getByPartnerId(1L)).willReturn(List.of(accommodation));

        // when & then
        mockMvc.perform(get("/api/v1/extranet/accommodations")
                        .header("X-Partner-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("서울 호텔"))
                .andDo(document("extranet/accommodation/list",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID"))));
    }

    @Test
    void 숙소_상세_조회_API() throws Exception {

        // given
        Accommodation accommodation = Accommodation.builder()
                .partnerId(1L)
                .name("서울 호텔")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구 테헤란로 123")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        given(getQuery.getById(eq(1L), eq(1L))).willReturn(accommodation);

        // when & then
        mockMvc.perform(get("/api/v1/extranet/accommodations/1")
                        .header("X-Partner-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("서울 호텔"))
                .andDo(document("extranet/accommodation/detail",
                        requestHeaders(
                                headerWithName("X-Partner-Id").description("파트너(업체) ID"))));
    }
}
