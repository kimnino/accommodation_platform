package com.accommodation.platform.scenario;

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

/**
 * 시나리오 테스트: 동시 예약 시 하나만 성공 (Pessimistic Lock 검증)
 * remaining_quantity=1 재고에 10개 스레드가 동시 예약 → 정확히 1개만 성공
 */
class ConcurrentReservationScenarioTest extends IntegrationTestBase {

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

    private static final LocalDate CHECK_IN_DATE = LocalDate.of(2026, 6, 1);
    private static final LocalDate CHECK_OUT_DATE = LocalDate.of(2026, 6, 2);

    private Long accommodationId;
    private Long roomOptionId;

    @BeforeEach
    void setUp() {
        // 데이터 초기화
        reservationJpaRepository.deleteAll();
        inventoryJpaRepository.deleteAll();
        roomPriceJpaRepository.deleteAll();
        roomOptionJpaRepository.deleteAll();
        roomJpaRepository.deleteAll();
        accommodationJpaRepository.deleteAll();

        // given: 숙소 저장
        AccommodationJpaEntity accommodation = accommodationJpaRepository.save(
                new AccommodationJpaEntity(
                        null, 1L, "동시성 테스트 호텔", AccommodationType.HOTEL,
                        null, "서울시 서초구 서초대로 100",
                        37.4923, 127.0292, "서초역 1번 출구",
                        AccommodationStatus.ACTIVE,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        accommodationId = accommodation.getId();

        // given: 객실 저장
        RoomJpaEntity room = roomJpaRepository.save(
                new RoomJpaEntity(null, accommodationId, "트윈 룸", "트윈", 2, 4, RoomStatus.ACTIVE)
        );

        // given: 객실 옵션 저장
        RoomOptionJpaEntity roomOption = roomOptionJpaRepository.save(
                new RoomOptionJpaEntity(
                        null, room.getId(), "기본",
                        CancellationPolicy.FREE_CANCELLATION,
                        BigDecimal.ZERO, null, null,
                        LocalTime.of(15, 0), LocalTime.of(11, 0)
                )
        );
        roomOptionId = roomOption.getId();

        // given: 재고 remaining_quantity=1 (임계 자원)
        inventoryJpaRepository.save(
                new InventoryJpaEntity(null, roomOptionId, CHECK_IN_DATE, 1, 1, InventoryStatus.AVAILABLE)
        );

        // given: STAY 가격 저장
        roomPriceJpaRepository.save(
                new RoomPriceJpaEntity(
                        null, roomOptionId, CHECK_IN_DATE,
                        PriceType.STAY,
                        new BigDecimal("100000"), new BigDecimal("90000"), true
                )
        );
    }

    @Test
    void 동시_예약_요청_시_하나만_성공해야_한다() throws InterruptedException {

        // given
        int concurrentThreads = 10;
        CountDownLatch readyLatch = new CountDownLatch(concurrentThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(concurrentThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);

        final Long capturedAccommodationId = accommodationId;
        final Long capturedRoomOptionId = roomOptionId;

        // when: 10개 스레드가 동시에 예약 요청
        for (int i = 0; i < concurrentThreads; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    // 모든 스레드가 준비될 때까지 대기 후 동시에 출발
                    readyLatch.countDown();
                    startLatch.await();

                    CreateStayReservationCommand command = new CreateStayReservationCommand(
                            "concurrent-req-" + index,
                            (long) (index + 100),
                            capturedAccommodationId,
                            capturedRoomOptionId,
                            CHECK_IN_DATE,
                            CHECK_OUT_DATE,
                            "동시투숙객" + index,
                            "010-0000-" + String.format("%04d", index),
                            "concurrent" + index + "@example.com"
                    );

                    createUseCase.createStayReservation(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비 완료 후 동시 시작
        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // then: 재고가 1개이므로 성공은 정확히 1개
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(concurrentThreads - 1);

        // then: DB remaining_quantity=0 확인
        InventoryJpaEntity inventory = inventoryJpaRepository
                .findByRoomOptionIdAndDateBetweenOrderByDateAsc(roomOptionId, CHECK_IN_DATE, CHECK_IN_DATE)
                .getFirst();
        assertThat(inventory.getRemainingQuantity()).isZero();
    }
}
