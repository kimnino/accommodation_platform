package com.accommodation.platform.core.room.domain.model;

import java.math.BigDecimal;

import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomOptionTest {

    @Test
    void 옵션_생성_시_추가금액_기본값은_0이다() {

        RoomOption option = RoomOption.builder()
                .roomId(1L)
                .name("기본")
                .cancellationPolicy(CancellationPolicy.FREE_CANCELLATION)
                .build();

        assertThat(option.getAdditionalPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void 옵션_정보를_수정할_수_있다() {

        RoomOption option = createOption();

        option.updateInfo("조식 포함", CancellationPolicy.NON_REFUNDABLE, new BigDecimal("20000"), null, null, null, null);

        assertThat(option.getName()).isEqualTo("조식 포함");
        assertThat(option.getCancellationPolicy()).isEqualTo(CancellationPolicy.NON_REFUNDABLE);
        assertThat(option.getAdditionalPrice()).isEqualByComparingTo(new BigDecimal("20000"));
    }

    @Test
    void 옵션명_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> RoomOption.builder()
                .roomId(1L)
                .name("")
                .cancellationPolicy(CancellationPolicy.FREE_CANCELLATION)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("옵션명");
    }

    @Test
    void 취소정책_누락_시_생성_실패한다() {

        assertThatThrownBy(() -> RoomOption.builder()
                .roomId(1L)
                .name("기본")
                .cancellationPolicy(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("취소 정책");
    }

    private RoomOption createOption() {

        return RoomOption.builder()
                .roomId(1L)
                .name("기본")
                .cancellationPolicy(CancellationPolicy.FREE_CANCELLATION)
                .additionalPrice(BigDecimal.ZERO)
                .build();
    }
}
