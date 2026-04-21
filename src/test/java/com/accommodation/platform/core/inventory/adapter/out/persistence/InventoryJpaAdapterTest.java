package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.inventory.domain.model.Inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class InventoryJpaAdapterTest extends IntegrationTestBase {

    @Autowired
    private InventoryJpaAdapter adapter;

    @Autowired
    private InventoryJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void 재고를_저장하고_조회한다() {

        // given
        Inventory inventory = Inventory.builder()
                .roomOptionId(1L)
                .date(LocalDate.of(2026, 5, 1))
                .totalQuantity(10)
                .remainingQuantity(10)
                .build();

        // when
        Inventory saved = adapter.save(inventory);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRoomOptionId()).isEqualTo(1L);
        assertThat(saved.getTotalQuantity()).isEqualTo(10);
        assertThat(saved.getRemainingQuantity()).isEqualTo(10);
    }

    @Test
    void 날짜_범위로_재고를_조회한다() {

        // given
        adapter.save(buildInventory(1L, LocalDate.of(2026, 5, 1), 5));
        adapter.save(buildInventory(1L, LocalDate.of(2026, 5, 2), 5));
        adapter.save(buildInventory(1L, LocalDate.of(2026, 5, 3), 5));
        adapter.save(buildInventory(2L, LocalDate.of(2026, 5, 1), 5));

        // when
        List<Inventory> result = adapter.findByRoomOptionIdAndDateRange(
                1L, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(inv -> inv.getRoomOptionId().equals(1L));
    }

    @Test
    void 비관적_락으로_재고를_조회한다() {

        // given
        Inventory inventory = adapter.save(buildInventory(10L, LocalDate.of(2026, 6, 1), 3));

        // when
        Optional<Inventory> found = adapter.findWithLock(10L, LocalDate.of(2026, 6, 1));

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(inventory.getId());
    }

    @Test
    void 재고_차감이_정확히_동작한다() {

        // given
        Inventory inventory = adapter.save(buildInventory(20L, LocalDate.of(2026, 7, 1), 5));
        Optional<Inventory> locked = adapter.findWithLock(20L, LocalDate.of(2026, 7, 1));
        assertThat(locked).isPresent();

        // when
        locked.get().decrease(2);
        adapter.save(locked.get());

        // then
        Optional<Inventory> updated = adapter.findById(inventory.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getRemainingQuantity()).isEqualTo(3);
    }

    @Test
    void roomOptionId로_재고를_전체_삭제한다() {

        // given
        adapter.save(buildInventory(30L, LocalDate.of(2026, 8, 1), 5));
        adapter.save(buildInventory(30L, LocalDate.of(2026, 8, 2), 5));

        // when
        adapter.deleteByRoomOptionId(30L);

        // then
        List<Inventory> result = adapter.findByRoomOptionIdAndDateRange(
                30L, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 2));
        assertThat(result).isEmpty();
    }

    // @Test — 트랜잭션 롤백 환경에서는 별도 스레드 트랜잭션 검증이 어려우므로
    // 비관적 락 동시성은 ConcurrencyReservationTest에서 end-to-end로 검증
    void 동시_차감_시_비관적_락으로_정확성을_보장한다() throws Exception {

        // given: 재고 2개
        adapter.save(buildInventory(40L, LocalDate.of(2026, 9, 1), 2));

        int threads = 5;
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    Optional<Inventory> inv = adapter.findWithLock(40L, LocalDate.of(2026, 9, 1));
                    inv.ifPresent(it -> {
                        it.decrease(1);
                        adapter.save(it);
                        successCount.incrementAndGet();
                    });
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 총 재고 2개 → 최대 2번만 성공해야 한다 (실제 검증은 ConcurrencyReservationTest 참조)
        assertThat(successCount.get()).isLessThanOrEqualTo(2);
    }

    private Inventory buildInventory(Long roomOptionId, LocalDate date, int quantity) {

        return Inventory.builder()
                .roomOptionId(roomOptionId)
                .date(date)
                .totalQuantity(quantity)
                .remainingQuantity(quantity)
                .build();
    }
}
