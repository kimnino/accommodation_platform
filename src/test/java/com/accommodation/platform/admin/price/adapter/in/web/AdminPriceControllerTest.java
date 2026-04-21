package com.accommodation.platform.admin.price.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.admin.price.application.port.in.AdminAdjustPriceUseCase;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;

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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPriceController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class AdminPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAdjustPriceUseCase adjustPriceUseCase;

    @Test
    void 관리자_가격_조정_API() throws Exception {

        // given
        RoomPrice price = RoomPrice.builder()
                .id(1L)
                .roomOptionId(10L)
                .date(LocalDate.of(2026, 5, 1))
                .priceType(PriceType.STAY)
                .basePrice(new BigDecimal("150000"))
                .sellingPrice(new BigDecimal("130000"))
                .taxIncluded(true)
                .build();

        given(adjustPriceUseCase.adjustPrice(eq(10L), any())).willReturn(List.of(price));

        // when & then
        mockMvc.perform(patch("/api/v1/admin/room-options/10/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "start_date": "2026-05-01",
                                  "end_date": "2026-05-01",
                                  "selling_price": 130000,
                                  "tax_included": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].selling_price").value(130000))
                .andDo(document("admin-price-adjust",
                        requestFields(
                                fieldWithPath("start_date").description("조정 시작일"),
                                fieldWithPath("end_date").description("조정 종료일"),
                                fieldWithPath("selling_price").description("판매 가격"),
                                fieldWithPath("tax_included").description("세금 포함 여부")),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data[].id").description("가격 ID").optional(),
                                fieldWithPath("data[].room_option_id").description("객실 옵션 ID"),
                                fieldWithPath("data[].date").description("적용 날짜"),
                                fieldWithPath("data[].price_type").description("가격 유형"),
                                fieldWithPath("data[].base_price").description("기본 가격"),
                                fieldWithPath("data[].selling_price").description("판매 가격"),
                                fieldWithPath("data[].tax_included").description("세금 포함 여부"),
                                fieldWithPath("data[].created_at").description("생성 시각").optional(),
                                fieldWithPath("data[].updated_at").description("수정 시각").optional(),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }
}
