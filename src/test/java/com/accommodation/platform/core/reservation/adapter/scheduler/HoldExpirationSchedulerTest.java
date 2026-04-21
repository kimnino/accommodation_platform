package com.accommodation.platform.core.reservation.adapter.scheduler;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaEntity;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaRepository;
import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;
import com.accommodation.platform.core.reservation.adapter.out.persistence.ReservationJpaEntity;
import com.accommodation.platform.core.reservation.adapter.out.persistence.ReservationJpaRepository;
import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class HoldExpirationSchedulerTest extends IntegrationTestBase {

    @Autowired
    private HoldExpirationScheduler scheduler;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private InventoryJpaRepository inventoryJpaRepository;

    @Test
    void 만료된_홀드_예약은_취소되고_날짜별_재고가_복구된다() {
        // given — 2박 예약, 재고 각 1개 (예약으로 차감된 상태: remaining=0)
        LocalDate checkIn = LocalDate.of(2026, 9, 1);
        LocalDate checkOut = LocalDate.of(2026, 9, 3);
        long roomOptionId = 9001L;

        InventoryJpaEntity inv1 = inventoryJpaRepository.save(
                new InventoryJpaEntity(null, roomOptionId, checkIn, 1, 0, InventoryStatus.AVAILABLE));
        InventoryJpaEntity inv2 = inventoryJpaRepository.save(
                new InventoryJpaEntity(null, roomOptionId, checkIn.plusDays(1), 1, 0, InventoryStatus.AVAILABLE));

        // holdExpiredAt = 11분 전 → 만료
        reservationJpaRepository.save(new ReservationJpaEntity(
                null, "RES-HOLD-EXP-001", "req-hold-exp-001",
                1L, 1L, roomOptionId,
                ReservationType.STAY, checkIn, checkOut,
                null, 0,
                "홍길동", "010-1234-5678", "test@test.com",
                BigDecimal.valueOf(200_000), ReservationStatus.PAYMENT_WAITING,
                Instant.now().minusSeconds(660)));

        // when
        scheduler.cancelExpiredHolds();

        // then — 예약 상태: PAYMENT_WAITING → CANCELLED
        List<ReservationJpaEntity> cancelled = reservationJpaRepository.findByStatus(ReservationStatus.CANCELLED);
        assertThat(cancelled).anyMatch(r -> r.getReservationRequestId().equals("req-hold-exp-001"));

        // then — 재고 복구: remaining 0 → 1
        InventoryJpaEntity restored1 = inventoryJpaRepository.findById(inv1.getId()).orElseThrow();
        InventoryJpaEntity restored2 = inventoryJpaRepository.findById(inv2.getId()).orElseThrow();
        assertThat(restored1.getRemainingQuantity()).isEqualTo(1);
        assertThat(restored2.getRemainingQuantity()).isEqualTo(1);
    }

    @Test
    void 만료되지_않은_홀드_예약은_취소되지_않고_재고도_유지된다() {
        // given — holdExpiredAt = 5분 후 → 아직 유효
        LocalDate checkIn = LocalDate.of(2026, 9, 10);
        LocalDate checkOut = LocalDate.of(2026, 9, 11);
        long roomOptionId = 9002L;

        InventoryJpaEntity inv = inventoryJpaRepository.save(
                new InventoryJpaEntity(null, roomOptionId, checkIn, 1, 0, InventoryStatus.AVAILABLE));

        ReservationJpaEntity active = reservationJpaRepository.save(new ReservationJpaEntity(
                null, "RES-HOLD-ACTIVE-001", "req-hold-active-001",
                2L, 1L, roomOptionId,
                ReservationType.STAY, checkIn, checkOut,
                null, 0,
                "김철수", "010-9876-5432", "active@test.com",
                BigDecimal.valueOf(100_000), ReservationStatus.PAYMENT_WAITING,
                Instant.now().plusSeconds(300)));

        // when
        scheduler.cancelExpiredHolds();

        // then — 예약 상태 유지
        ReservationJpaEntity result = reservationJpaRepository.findById(active.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PAYMENT_WAITING);

        // then — 재고 그대로 (복구 없음)
        InventoryJpaEntity unchanged = inventoryJpaRepository.findById(inv.getId()).orElseThrow();
        assertThat(unchanged.getRemainingQuantity()).isZero();
    }
}
