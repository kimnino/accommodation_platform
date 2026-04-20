package com.accommodation.platform.core.inventory.domain.model;

import java.time.LocalDate;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Inventory extends BaseEntity {

    private Long id;
    private Long roomOptionId;
    private LocalDate date;
    private int totalQuantity;
    private int remainingQuantity;
    private InventoryStatus status;

    @Builder
    public Inventory(Long id, Long roomOptionId, LocalDate date,
                     int totalQuantity, int remainingQuantity) {

        validateRequired(roomOptionId, date);
        validateQuantity(totalQuantity, remainingQuantity);
        this.id = id;
        this.roomOptionId = roomOptionId;
        this.date = date;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.status = remainingQuantity > 0 ? InventoryStatus.AVAILABLE : InventoryStatus.SOLD_OUT;
        initTimestamps();
    }

    public void decrease(int quantity) {

        if (this.remainingQuantity - quantity < 0) {
            throw new IllegalStateException("잔여 재고가 부족합니다. 현재: " + remainingQuantity + ", 요청: " + quantity);
        }
        this.remainingQuantity -= quantity;
        refreshStatus();
        updateTimestamp();
    }

    public void increase(int quantity) {

        if (this.remainingQuantity + quantity > this.totalQuantity) {
            throw new IllegalStateException("총 재고를 초과할 수 없습니다.");
        }
        this.remainingQuantity += quantity;
        refreshStatus();
        updateTimestamp();
    }

    public void updateTotalQuantity(int totalQuantity) {

        if (totalQuantity < 0) {
            throw new IllegalArgumentException("총 재고는 0 이상이어야 합니다.");
        }
        this.totalQuantity = totalQuantity;
        if (this.remainingQuantity > totalQuantity) {
            this.remainingQuantity = totalQuantity;
        }
        refreshStatus();
        updateTimestamp();
    }

    public void close() {

        this.status = InventoryStatus.CLOSED;
        updateTimestamp();
    }

    public void reopen() {

        this.status = this.remainingQuantity > 0 ? InventoryStatus.AVAILABLE : InventoryStatus.SOLD_OUT;
        updateTimestamp();
    }

    public boolean isAvailable() {

        return this.status == InventoryStatus.AVAILABLE && this.remainingQuantity > 0;
    }

    private void refreshStatus() {

        if (this.status == InventoryStatus.CLOSED) {
            return;
        }
        this.status = this.remainingQuantity > 0 ? InventoryStatus.AVAILABLE : InventoryStatus.SOLD_OUT;
    }

    private void validateRequired(Long roomOptionId, LocalDate date) {

        if (roomOptionId == null) {
            throw new IllegalArgumentException("roomOptionId는 필수입니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }
    }

    private void validateQuantity(int totalQuantity, int remainingQuantity) {

        if (totalQuantity < 0) {
            throw new IllegalArgumentException("총 재고는 0 이상이어야 합니다.");
        }
        if (remainingQuantity < 0) {
            throw new IllegalArgumentException("잔여 재고는 0 이상이어야 합니다.");
        }
        if (remainingQuantity > totalQuantity) {
            throw new IllegalArgumentException("잔여 재고가 총 재고를 초과할 수 없습니다.");
        }
    }
}
