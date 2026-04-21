package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.AccommodationDetail;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.ImageInfo;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.OptionWithPrice;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.RoomImageInfo;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery.RoomWithOptions;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.AccommodationSummary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerAccommodationController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class CustomerAccommodationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerSearchAccommodationQuery searchQuery;

    @MockitoBean
    private CustomerGetAccommodationDetailQuery detailQuery;

    @Test
    void 숙소_검색_API() throws Exception {

        // given
        AccommodationSummary summary = new AccommodationSummary(
                1L, "서울 호텔", "HOTEL", "서울시 강남구 테헤란로 123",
                37.5665, 126.9780,
                "/accommodation/exterior/main.png", 120000L, true);

        given(searchQuery.search(any(), any()))
                .willReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 20), 1));

        // when & then
        mockMvc.perform(get("/api/v1/accommodations")
                        .param("region", "강남")
                        .param("checkInDate", "2026-04-25")
                        .param("checkOutDate", "2026-04-27")
                        .param("guestCount", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].name").value("서울 호텔"))
                .andDo(document("customer/accommodation/search",
                        queryParameters(
                                parameterWithName("region").description("지역 검색어").optional(),
                                parameterWithName("checkInDate").description("체크인 날짜 (yyyy-MM-dd)").optional(),
                                parameterWithName("checkOutDate").description("체크아웃 날짜 (yyyy-MM-dd)").optional(),
                                parameterWithName("guestCount").description("투숙 인원").optional())));
    }

    @Test
    void 숙소_상세_조회_API() throws Exception {

        // given
        OptionWithPrice option = new OptionWithPrice(
                1L, "조식 포함", "FREE_CANCELLATION",
                new BigDecimal("240000"), BigDecimal.ZERO, 3);

        RoomWithOptions room = new RoomWithOptions(
                1L, "디럭스 더블", "디럭스", 2, 4,
                List.of(new RoomImageInfo("/room/deluxe/1.png", 1, true)),
                List.of(option));

        AccommodationDetail detail = new AccommodationDetail(
                1L, "서울 호텔", "HOTEL", "ACTIVE",
                "서울시 강남구 테헤란로 123", 37.5665, 126.9780,
                "강남역 5번 출구 도보 3분",
                List.of(new ImageInfo("/accommodation/exterior/main.png", "EXTERIOR", 1, true)),
                List.of(room));

        given(detailQuery.getDetail(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/accommodations/1")
                        .param("checkInDate", "2026-04-25")
                        .param("checkOutDate", "2026-04-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("서울 호텔"))
                .andExpect(jsonPath("$.data.rooms[0].room_name").value("디럭스 더블"))
                .andExpect(jsonPath("$.data.rooms[0].images[0].relative_path").value("/room/deluxe/1.png"))
                .andExpect(jsonPath("$.data.rooms[0].options[0].total_price").value(240000))
                .andDo(document("customer/accommodation/detail",
                        queryParameters(
                                parameterWithName("checkInDate").description("체크인 날짜 (yyyy-MM-dd)"),
                                parameterWithName("checkOutDate").description("체크아웃 날짜 (yyyy-MM-dd)"))));
    }
}
