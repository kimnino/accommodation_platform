package com.accommodation.platform.core.tag.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TagTest {

    @Test
    void 태그_생성_시_활성_상태여야_한다() {

        Tag tag = Tag.builder()
                .tagGroupId(1L)
                .name("수영장")
                .displayOrder(1)
                .build();

        assertThat(tag.isActive()).isTrue();
        assertThat(tag.getName()).isEqualTo("수영장");
    }

    @Test
    void 태그_정보를_수정할_수_있다() {

        Tag tag = Tag.builder()
                .tagGroupId(1L)
                .name("수영장")
                .displayOrder(1)
                .build();

        tag.updateInfo("실내 수영장", 2);

        assertThat(tag.getName()).isEqualTo("실내 수영장");
        assertThat(tag.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void 태그명_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> Tag.builder()
                .tagGroupId(1L)
                .name("")
                .displayOrder(1)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("태그명");
    }
}
