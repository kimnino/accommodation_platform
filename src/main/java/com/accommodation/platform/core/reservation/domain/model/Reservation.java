package com.accommodation.platform.core.reservation.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Reservation extends BaseEntity {

    private Long id;
    private String reservationNumber;
    private String reservationRequestId;
    private Long memberId;
    private Long accommodationId;
    private Long roomOptionId;
    private ReservationType reservationType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalTime hourlyStartTime;
    private int hourlyUsageMinutes;
    private GuestInfo guestInfo;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private Instant holdExpiredAt;

    @Builder
    public Reservation(Long id, String reservationRequestId, Long memberId,
                       Long accommodationId, Long roomOptionId, ReservationType reservationType,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       LocalTime hourlyStartTime, int hourlyUsageMinutes,
                       GuestInfo guestInfo, BigDecimal totalPrice) {

        validateRequired(memberId, accommodationId, roomOptionId, reservationType, guestInfo, totalPrice);
        this.id = id;
        this.reservationNumber = generateReservationNumber();
        this.reservationRequestId = reservationRequestId != null ? reservationRequestId : UUID.randomUUID().toString();
        this.memberId = memberId;
        this.accommodationId = accommodationId;
        this.roomOptionId = roomOptionId;
        this.reservationType = reservationType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.hourlyStartTime = hourlyStartTime;
        this.hourlyUsageMinutes = hourlyUsageMinutes;
        this.guestInfo = guestInfo;
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.PENDING;
        initTimestamps();
    }

    public void holdForPayment(Instant expiredAt) {

        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 결제 대기로 전환할 수 있습니다.");
        }
        this.status = ReservationStatus.PAYMENT_WAITING;
        this.holdExpiredAt = expiredAt;
        updateTimestamp();
    }

    public void confirm() {

        if (this.status != ReservationStatus.PAYMENT_WAITING) {
            throw new IllegalStateException("PAYMENT_WAITING 상태에서만 확정할 수 있습니다.");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.holdExpiredAt = null;
        updateTimestamp();
    }

    public void cancel() {

        if (this.status == ReservationStatus.CANCELLED || this.status == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("이미 취소되었거나 완료된 예약입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        updateTimestamp();
    }

    public void complete() {

        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("CONFIRMED 상태에서만 완료 처리할 수 있습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
        updateTimestamp();
    }

    public void markNoShow() {

        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("CONFIRMED 상태에서만 노쇼 처리할 수 있습니다.");
        }
        this.status = ReservationStatus.NO_SHOW;
        updateTimestamp();
    }

    public boolean isHoldExpired() {

        return this.status == ReservationStatus.PAYMENT_WAITING
                && this.holdExpiredAt != null
                && Instant.now().isAfter(this.holdExpiredAt);
    }

    public static Reservation reconstruct(Long id, String reservationNumber, String reservationRequestId,
                                          Long memberId, Long accommodationId, Long roomOptionId,
                                          ReservationType reservationType,
                                          LocalDate checkInDate, LocalDate checkOutDate,
                                          LocalTime hourlyStartTime, int hourlyUsageMinutes,
                                          GuestInfo guestInfo, BigDecimal totalPrice,
                                          ReservationStatus status, Instant holdExpiredAt) {

        Reservation r = new Reservation(id, reservationRequestId, memberId, accommodationId, roomOptionId,
                reservationType, checkInDate, checkOutDate, hourlyStartTime, hourlyUsageMinutes, guestInfo, totalPrice);
        r.reservationNumber = reservationNumber;
        r.status = status;
        r.holdExpiredAt = holdExpiredAt;
        return r;
    }

    private String generateReservationNumber() {

        return "RSV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void validateRequired(Long memberId, Long accommodationId, Long roomOptionId,
                                   ReservationType reservationType, GuestInfo guestInfo, BigDecimal totalPrice) {

        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("accommodationId는 필수입니다.");
        }
        if (roomOptionId == null) {
            throw new IllegalArgumentException("roomOptionId는 필수입니다.");
        }
        if (reservationType == null) {
            throw new IllegalArgumentException("예약 유형은 필수입니다.");
        }
        if (guestInfo == null) {
            throw new IllegalArgumentException("투숙객 정보는 필수입니다.");
        }
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("총 금액은 0 이상이어야 합니다.");
        }
    }
}
