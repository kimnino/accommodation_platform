package com.accommodation.platform.core.inventory.domain.model;

import java.time.LocalDate;

import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryTest {

    @Test
    void 재고_생성_시_잔여수량이_있으면_AVAILABLE이다() {

        Inventory inventory = createInventory(5, 5);

        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.AVAILABLE);
        assertThat(inventory.isAvailable()).isTrue();
    }

    @Test
    void 재고_차감_시_잔여수량이_감소한다() {

        Inventory inventory = createInventory(5, 5);

        inventory.decrease(3);

        assertThat(inventory.getRemainingQuantity()).isEqualTo(2);
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.AVAILABLE);
    }

    @Test
    void 재고_차감_시_0이면_SOLD_OUT이다() {

        Inventory inventory = createInventory(5, 5);

        inventory.decrease(5);

        assertThat(inventory.getRemainingQuantity()).isZero();
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.SOLD_OUT);
        assertThat(inventory.isAvailable()).isFalse();
    }

    @Test
    void 잔여_재고보다_많이_차감하면_예외가_발생한다() {

        Inventory inventory = createInventory(5, 3);

        assertThatThrownBy(() -> inventory.decrease(4))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("잔여 재고가 부족");
    }

    @Test
    void 재고_복구_시_잔여수량이_증가한다() {

        Inventory inventory = createInventory(5, 2);

        inventory.increase(2);

        assertThat(inventory.getRemainingQuantity()).isEqualTo(4);
    }

    @Test
    void 총_재고를_초과하여_복구하면_예외가_발생한다() {

        Inventory inventory = createInventory(5, 4);

        assertThatThrownBy(() -> inventory.increase(2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("총 재고를 초과");
    }

    @Test
    void 재고를_마감하고_다시_열_수_있다() {

        Inventory inventory = createInventory(5, 5);

        inventory.close();
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.CLOSED);
        assertThat(inventory.isAvailable()).isFalse();

        inventory.reopen();
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.AVAILABLE);
    }

    @Test
    void 총_재고_수정_시_잔여수량이_초과하면_조정된다() {

        Inventory inventory = createInventory(10, 8);

        inventory.updateTotalQuantity(5);

        assertThat(inventory.getTotalQuantity()).isEqualTo(5);
        assertThat(inventory.getRemainingQuantity()).isEqualTo(5);
    }

    private Inventory createInventory(int total, int remaining) {

        return Inventory.builder()
                .roomOptionId(1L)
                .date(LocalDate.of(2026, 4, 20))
                .totalQuantity(total)
                .remainingQuantity(remaining)
                .build();
    }
}
