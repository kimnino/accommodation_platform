package com.accommodation.platform.customer.reservation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationJpaEntity;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationJpaRepository;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaEntity;
import com.accommodation.platform.core.inventory.adapter.out.persistence.InventoryJpaRepository;
import com.accommodation.platform.core.inventory.domain.enums.InventoryStatus;
import com.accommodation.platform.core.price.adapter.out.persistence.RoomPriceJpaEntity;
import com.accommodation.platform.core.price.adapter.out.persistence.RoomPriceJpaRepository;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.reservation.adapter.out.persistence.ReservationJpaRepository;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomJpaRepository;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionJpaRepository;
import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;
import com.accommodation.platform.core.room.domain.enums.RoomStatus;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase;
import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase.CreateStayReservationCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrencyReservationTest extends IntegrationTestBase {

    @Autowired
    private CustomerCreateReservationUseCase createUseCase;

    @Autowired
    private AccommodationJpaRepository accommodationJpaRepository;

    @Autowired
    private RoomJpaRepository roomJpaRepository;

    @Autowired
    private RoomOptionJpaRepository roomOptionJpaRepository;

    @Autowired
    private InventoryJpaRepository inventoryJpaRepository;

    @Autowired
    private RoomPriceJpaRepository roomPriceJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    private Long accommodationId;
    private Long roomOptionId;

    private static final LocalDate CHECK_IN_DATE = LocalDate.of(2026, 5, 1);
    private static final LocalDate CHECK_OUT_DATE = LocalDate.of(2026, 5, 2);

    @BeforeEach
    void setUp() {
        reservationJpaRepository.deleteAll();
        inventoryJpaRepository.deleteAll();
        roomPriceJpaRepository.deleteAll();
        roomOptionJpaRepository.deleteAll();
        roomJpaRepository.deleteAll();
        accommodationJpaRepository.deleteAll();

        AccommodationJpaEntity accommodation = accommodationJpaRepository.save(
                new AccommodationJpaEntity(
                        null, 1L, "동시성 테스트 호텔", AccommodationType.HOTEL,
                        null, "서울시 강남구 테헤란로 100",
                        37.5, 127.0, "강남역 1번 출구",
                        AccommodationStatus.ACTIVE,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        accommodationId = accommodation.getId();

        RoomJpaEntity room = roomJpaRepository.save(
                new RoomJpaEntity(null, accommodationId, "스탠다드", "스탠다드", 2, 4, RoomStatus.ACTIVE)
        );

        RoomOptionJpaEntity roomOption = roomOptionJpaRepository.save(
                new RoomOptionJpaEntity(
                        null, room.getId(), "기본",
                        CancellationPolicy.FREE_CANCELLATION,
                        BigDecimal.ZERO, null, null,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        roomOptionId = roomOption.getId();

        int totalInventory = 3;
        inventoryJpaRepository.save(new InventoryJpaEntity(
                null, roomOptionId, CHECK_IN_DATE, totalInventory, totalInventory, InventoryStatus.AVAILABLE));

        roomPriceJpaRepository.save(
                new RoomPriceJpaEntity(
                        null, roomOptionId, CHECK_IN_DATE,
                        PriceType.STAY,
                        new BigDecimal("100000"), new BigDecimal("90000"), true
                )
        );
    }

    @Test
    void 동시_예약_요청_시_재고_수만큼만_성공해야_한다() throws InterruptedException {

        // given
        int totalInventory = 3;
        int concurrentRequests = 10;

        final Long capturedAccommodationId = accommodationId;
        final Long capturedRoomOptionId = roomOptionId;

        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch readyLatch = new CountDownLatch(concurrentRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < concurrentRequests; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    CreateStayReservationCommand command = new CreateStayReservationCommand(
                            "req-" + index,
                            (long) index + 1,
                            capturedAccommodationId,
                            capturedRoomOptionId,
                            CHECK_IN_DATE,
                            CHECK_OUT_DATE,
                            "투숙객" + index,
                            "010-0000-000" + index,
                            "guest" + index + "@test.com");

                    createUseCase.createStayReservation(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(totalInventory);
        assertThat(failCount.get()).isEqualTo(concurrentRequests - totalInventory);

        InventoryJpaEntity inventory = inventoryJpaRepository
                .findByRoomOptionIdAndDateBetweenOrderByDateAsc(capturedRoomOptionId, CHECK_IN_DATE, CHECK_IN_DATE)
                .getFirst();
        assertThat(inventory.getRemainingQuantity()).isZero();
    }
}
