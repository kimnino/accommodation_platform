package com.accommodation.platform.core.reservation.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    @Test
    void 예약_생성_시_PENDING_상태여야_한다() {

        Reservation reservation = createReservation();

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getReservationNumber()).startsWith("RSV-");
    }

    @Test
    void PENDING에서_결제대기로_전환할_수_있다() {

        Reservation reservation = createReservation();
        Instant expiredAt = Instant.now().plus(10, ChronoUnit.MINUTES);

        reservation.holdForPayment(expiredAt);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAYMENT_WAITING);
        assertThat(reservation.getHoldExpiredAt()).isEqualTo(expiredAt);
    }

    @Test
    void PAYMENT_WAITING에서_확정할_수_있다() {

        Reservation reservation = createReservation();
        reservation.holdForPayment(Instant.now().plus(10, ChronoUnit.MINUTES));

        reservation.confirm();

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getHoldExpiredAt()).isNull();
    }

    @Test
    void CONFIRMED에서_완료할_수_있다() {

        Reservation reservation = createReservation();
        reservation.holdForPayment(Instant.now().plus(10, ChronoUnit.MINUTES));
        reservation.confirm();

        reservation.complete();

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
    }

    @Test
    void 취소된_예약은_다시_취소할_수_없다() {

        Reservation reservation = createReservation();
        reservation.cancel();

        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 취소");
    }

    @Test
    void Hold_만료_여부를_확인할_수_있다() {

        Reservation reservation = createReservation();
        reservation.holdForPayment(Instant.now().minus(1, ChronoUnit.MINUTES));

        assertThat(reservation.isHoldExpired()).isTrue();
    }

    @Test
    void Hold_미만료_상태를_확인할_수_있다() {

        Reservation reservation = createReservation();
        reservation.holdForPayment(Instant.now().plus(10, ChronoUnit.MINUTES));

        assertThat(reservation.isHoldExpired()).isFalse();
    }

    @Test
    void 멱등성_키가_자동_생성된다() {

        Reservation reservation = createReservation();

        assertThat(reservation.getReservationRequestId()).isNotNull();
    }

    private Reservation createReservation() {

        return Reservation.builder()
                .memberId(1L)
                .accommodationId(1L)
                .roomOptionId(1L)
                .reservationType(ReservationType.STAY)
                .checkInDate(LocalDate.of(2026, 4, 25))
                .checkOutDate(LocalDate.of(2026, 4, 27))
                .guestInfo(new GuestInfo("홍길동", "010-1234-5678", "hong@test.com"))
                .totalPrice(new BigDecimal("240000"))
                .build();
    }
}
