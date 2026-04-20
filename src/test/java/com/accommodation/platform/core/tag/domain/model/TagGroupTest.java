package com.accommodation.platform.core.tag.domain.model;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TagGroupTest {

    @Test
    void 태그_그룹_생성_시_활성_상태여야_한다() {

        TagGroup tagGroup = createTagGroup();

        assertThat(tagGroup.isActive()).isTrue();
        assertThat(tagGroup.getName()).isEqualTo("공용시설");
        assertThat(tagGroup.getTargetType()).isEqualTo(TagTarget.ACCOMMODATION);
    }

    @Test
    void 태그_그룹을_비활성화하고_다시_활성화할_수_있다() {

        TagGroup tagGroup = createTagGroup();

        tagGroup.deactivate();
        assertThat(tagGroup.isActive()).isFalse();

        tagGroup.activate();
        assertThat(tagGroup.isActive()).isTrue();
    }

    @Test
    void 숙소유형_없이_전체_대상으로_생성할_수_있다() {

        TagGroup tagGroup = TagGroup.builder()
                .name("공통 시설")
                .displayOrder(1)
                .targetType(TagTarget.ACCOMMODATION)
                .accommodationType(null)
                .build();

        assertThat(tagGroup.getAccommodationType()).isNull();
    }

    @Test
    void 그룹명_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> TagGroup.builder()
                .name("")
                .displayOrder(1)
                .targetType(TagTarget.ACCOMMODATION)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("태그 그룹명");
    }

    private TagGroup createTagGroup() {

        return TagGroup.builder()
                .name("공용시설")
                .displayOrder(1)
                .targetType(TagTarget.ACCOMMODATION)
                .accommodationType(AccommodationType.HOTEL)
                .build();
    }
}
