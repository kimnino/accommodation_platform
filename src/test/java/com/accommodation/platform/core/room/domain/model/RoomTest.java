package com.accommodation.platform.core.room.domain.model;

import com.accommodation.platform.core.room.domain.enums.RoomStatus;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomTest {

    @Test
    void 객실_생성_시_ACTIVE_상태여야_한다() {

        Room room = createRoom();

        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
        assertThat(room.getCreatedAt()).isNotNull();
    }

    @Test
    void 객실을_비활성화하고_다시_활성화할_수_있다() {

        Room room = createRoom();

        room.deactivate();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.INACTIVE);

        room.activate();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
    }

    @Test
    void 객실_정보를_수정할_수_있다() {

        Room room = createRoom();

        room.updateInfo("수정된 객실", "스위트", 3, 5);

        assertThat(room.getName()).isEqualTo("수정된 객실");
        assertThat(room.getRoomTypeName()).isEqualTo("스위트");
        assertThat(room.getStandardCapacity()).isEqualTo(3);
        assertThat(room.getMaxCapacity()).isEqualTo(5);
    }

    @Test
    void 객실유형명_없이_생성할_수_있다() {

        Room room = Room.builder()
                .accommodationId(1L)
                .name("101호")
                .roomTypeName(null)
                .standardCapacity(2)
                .maxCapacity(4)
                .build();

        assertThat(room.getRoomTypeName()).isNull();
    }

    @Test
    void 기준_인원이_0이면_예외가_발생한다() {

        assertThatThrownBy(() -> Room.builder()
                .accommodationId(1L)
                .name("테스트")
                .roomTypeName("스탠다드")
                .standardCapacity(0)
                .maxCapacity(2)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기준 인원");
    }

    @Test
    void 최대_인원이_기준_인원보다_작으면_예외가_발생한다() {

        assertThatThrownBy(() -> Room.builder()
                .accommodationId(1L)
                .name("테스트")
                .roomTypeName("스탠다드")
                .standardCapacity(4)
                .maxCapacity(2)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 인원");
    }

    @Test
    void 객실명_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> Room.builder()
                .accommodationId(1L)
                .name("")
                .roomTypeName("스탠다드")
                .standardCapacity(2)
                .maxCapacity(4)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("객실명");
    }

    @Test
    void 태그를_추가하고_중복은_무시한다() {

        Room room = createRoom();

        room.addTagId(1L);
        room.addTagId(2L);
        room.addTagId(1L);

        assertThat(room.getTagIds()).hasSize(2);
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
}
