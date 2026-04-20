package com.accommodation.platform.customer.reservation.application.service;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaEntity;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaRepository;
import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase.CreateStayReservationCommand;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrencyReservationTest extends IntegrationTestBase {

    @Autowired
    private CustomerCreateReservationUseCase createUseCase;

    @Autowired
    private InventoryJpaRepository inventoryJpaRepository;

    @Test
    void 동시_예약_요청_시_재고_수만큼만_성공해야_한다() throws InterruptedException {

        // given
        int totalInventory = 3;
        int concurrentRequests = 10;
        LocalDate checkInDate = LocalDate.of(2026, 5, 1);
        LocalDate checkOutDate = LocalDate.of(2026, 5, 2);

        inventoryJpaRepository.save(new InventoryJpaEntity(
                null, 1L, checkInDate, totalInventory, totalInventory, InventoryStatus.AVAILABLE));

        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < concurrentRequests; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    CreateStayReservationCommand command = new CreateStayReservationCommand(
                            "req-" + index,
                            (long) index + 1,
                            1L,
                            1L,
                            checkInDate,
                            checkOutDate,
                            "투숙객" + index,
                            "010-0000-000" + index,
                            "guest" + index + "@test.com");

                    createUseCase.createStayReservation(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(totalInventory);
        assertThat(failCount.get()).isEqualTo(concurrentRequests - totalInventory);

        InventoryJpaEntity inventory = inventoryJpaRepository
                .findByRoomOptionIdAndDateBetweenOrderByDateAsc(1L, checkInDate, checkInDate)
                .getFirst();
        assertThat(inventory.getRemainingQuantity()).isZero();
    }
}
