package com.accommodation.platform.core.accommodation.domain.model;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.enums.ImageCategory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccommodationTest {

    @Test
    void 숙소_생성_시_PENDING_상태여야_한다() {

        Accommodation accommodation = createAccommodation();

        assertThat(accommodation.getStatus()).isEqualTo(AccommodationStatus.PENDING);
        assertThat(accommodation.getCreatedAt()).isNotNull();
        assertThat(accommodation.getUpdatedAt()).isNotNull();
    }

    @Test
    void PENDING_상태에서_활성화할_수_있다() {

        Accommodation accommodation = createAccommodation();

        accommodation.activate();

        assertThat(accommodation.getStatus()).isEqualTo(AccommodationStatus.ACTIVE);
    }

    @Test
    void SUSPENDED_상태에서_활성화할_수_있다() {

        Accommodation accommodation = createAccommodation();
        accommodation.activate();
        accommodation.suspend();

        accommodation.activate();

        assertThat(accommodation.getStatus()).isEqualTo(AccommodationStatus.ACTIVE);
    }

    @Test
    void ACTIVE_상태에서만_정지할_수_있다() {

        Accommodation accommodation = createAccommodation();

        assertThatThrownBy(accommodation::suspend)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACTIVE 상태에서만");
    }

    @Test
    void CLOSED_상태에서_활성화할_수_없다() {

        Accommodation accommodation = createAccommodation();
        accommodation.activate();
        accommodation.close();

        assertThatThrownBy(accommodation::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING 또는 SUSPENDED");
    }

    @Test
    void 이미_폐쇄된_숙소는_다시_폐쇄할_수_없다() {

        Accommodation accommodation = createAccommodation();
        accommodation.activate();
        accommodation.close();

        assertThatThrownBy(accommodation::close)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACTIVE 또는 SUSPENDED");
    }

    @Test
    void 필수값_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> Accommodation.builder()
                .partnerId(null)
                .name("테스트 호텔")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("partnerId");
    }

    @Test
    void 숙소명_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> Accommodation.builder()
                .partnerId(1L)
                .name("")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("숙소명");
    }

    @Test
    void 이미지를_추가하고_삭제할_수_있다() {

        Accommodation accommodation = createAccommodation();
        AccommodationImage image = new AccommodationImage(
                "/accommodation/exterior/20260420.png", ImageCategory.EXTERIOR, 1, true);

        accommodation.addImage(image);

        assertThat(accommodation.getImages()).hasSize(1);

        accommodation.removeImage("/accommodation/exterior/20260420.png");

        assertThat(accommodation.getImages()).isEmpty();
    }

    @Test
    void 태그를_추가하고_삭제할_수_있다() {

        Accommodation accommodation = createAccommodation();

        accommodation.addTagId(1L);
        accommodation.addTagId(2L);
        accommodation.addTagId(1L); // 중복 추가 무시

        assertThat(accommodation.getTagIds()).hasSize(2);

        accommodation.removeTagId(1L);

        assertThat(accommodation.getTagIds()).hasSize(1);
    }

    private Accommodation createAccommodation() {

        return Accommodation.builder()
                .partnerId(1L)
                .name("테스트 호텔")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구 테헤란로 123")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
    }
}
