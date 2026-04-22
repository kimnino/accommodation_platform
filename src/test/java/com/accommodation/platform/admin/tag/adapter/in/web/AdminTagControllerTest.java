package com.accommodation.platform.admin.tag.adapter.in.web;

import java.util.List;

import com.accommodation.platform.admin.tag.application.port.in.AdminGetTagGroupQuery;
import com.accommodation.platform.admin.tag.application.port.in.AdminGetTagQuery;
import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase;
import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagUseCase;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(AdminTagController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class AdminTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminManageTagGroupUseCase tagGroupUseCase;

    @MockitoBean
    private AdminGetTagGroupQuery tagGroupQuery;

    @MockitoBean
    private AdminManageTagUseCase tagUseCase;

    @MockitoBean
    private AdminGetTagQuery tagQuery;

    @Test
    void 태그_그룹_생성_API() throws Exception {

        // given
        TagGroup tagGroup = TagGroup.builder()
                .name("공용시설")
                .displayOrder(1)
                .targetType(TagTarget.ACCOMMODATION)
                .accommodationType(AccommodationType.HOTEL)
                .build();

        given(tagGroupUseCase.create(any())).willReturn(tagGroup);

        // when & then
        mockMvc.perform(post("/api/v1/admin/tag-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "공용시설",
                                    "display_order": 1,
                                    "target_type": "ACCOMMODATION",
                                    "accommodation_type": "HOTEL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("공용시설"))
                .andDo(document("admin/tag-group/create",
                        requestFields(
                                fieldWithPath("name").description("태그 그룹명"),
                                fieldWithPath("display_order").description("노출 순서"),
                                fieldWithPath("target_type").description("태그 대상 (ACCOMMODATION, ROOM)"),
                                fieldWithPath("accommodation_type").description("대상 숙소유형 (null이면 전체)").optional()),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data.id").description("태그 그룹 ID"),
                                fieldWithPath("data.name").description("태그 그룹명"),
                                fieldWithPath("data.display_order").description("노출 순서"),
                                fieldWithPath("data.target_type").description("태그 대상"),
                                fieldWithPath("data.accommodation_type").description("대상 숙소유형").optional(),
                                fieldWithPath("data.supplier_id").description("공급사 ID (공급사 전용 태그일 경우)").optional(),
                                fieldWithPath("data.is_active").description("활성 여부"),
                                fieldWithPath("data.created_at").description("생성 시각"),
                                fieldWithPath("data.updated_at").description("수정 시각"),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }

    @Test
    void 태그_그룹_목록_조회_API() throws Exception {

        // given
        TagGroup tagGroup = TagGroup.builder()
                .name("공용시설")
                .displayOrder(1)
                .targetType(TagTarget.ACCOMMODATION)
                .build();

        given(tagGroupQuery.listAll()).willReturn(List.of(tagGroup));

        // when & then
        mockMvc.perform(get("/api/v1/admin/tag-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("공용시설"))
                .andDo(document("admin/tag-group/list"));
    }

    @Test
    void 태그_생성_API() throws Exception {

        // given
        Tag tag = Tag.builder()
                .tagGroupId(1L)
                .name("수영장")
                .displayOrder(1)
                .build();

        given(tagUseCase.create(eq(1L), any())).willReturn(tag);

        // when & then
        mockMvc.perform(post("/api/v1/admin/tag-groups/1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "수영장",
                                    "display_order": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("수영장"))
                .andDo(document("admin/tag/create",
                        requestFields(
                                fieldWithPath("name").description("태그명"),
                                fieldWithPath("display_order").description("노출 순서")),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("data.id").description("태그 ID"),
                                fieldWithPath("data.tag_group_id").description("태그 그룹 ID"),
                                fieldWithPath("data.name").description("태그명"),
                                fieldWithPath("data.display_order").description("노출 순서"),
                                fieldWithPath("data.is_active").description("활성 여부"),
                                fieldWithPath("data.created_at").description("생성 시각"),
                                fieldWithPath("data.updated_at").description("수정 시각"),
                                fieldWithPath("error").description("에러 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시각"))));
    }

    @Test
    void 태그_목록_조회_API() throws Exception {

        // given
        Tag tag = Tag.builder()
                .tagGroupId(1L)
                .name("수영장")
                .displayOrder(1)
                .build();

        given(tagQuery.listByTagGroupId(1L)).willReturn(List.of(tag));

        // when & then
        mockMvc.perform(get("/api/v1/admin/tag-groups/1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("수영장"))
                .andDo(document("admin/tag/list"));
    }
}
