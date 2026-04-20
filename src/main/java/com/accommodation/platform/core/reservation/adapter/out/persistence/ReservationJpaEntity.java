package com.accommodation.platform.core.reservation.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.enums.ReservationType;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 예약 테이블.
 * 숙박/대실 예약을 통합 관리. reservationType으로 구분.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "reservation")
public class ReservationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 예약 번호 (사용자 노출용) */
    @Column(nullable = false, unique = true)
    private String reservationNumber;

    /** 멱등성 키 (클라이언트 발급 UUID, 중복 예약 방지) */
    @Column(unique = true)
    private String reservationRequestId;

    /** 회원 ID */
    @Column(nullable = false)
    private Long memberId;

    /** 숙소 ID */
    @Column(nullable = false)
    private Long accommodationId;

    /** 객실 옵션 ID */
    @Column(nullable = false)
    private Long roomOptionId;

    /** 예약 유형 (STAY: 숙박, HOURLY: 대실) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationType reservationType;

    /** 체크인 날짜 */
    private LocalDate checkInDate;

    /** 체크아웃 날짜 */
    private LocalDate checkOutDate;

    /** 대실 시작 시간 (대실 예약 시) */
    private LocalTime hourlyStartTime;

    /** 대실 이용 시간(분) */
    private int hourlyUsageMinutes;

    /** 투숙객 이름 */
    @Column(nullable = false)
    private String guestName;

    /** 투숙객 연락처 */
    @Column(nullable = false)
    private String guestPhone;

    /** 투숙객 이메일 */
    private String guestEmail;

    /** 총 결제 금액 */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    /** 예약 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    /** 재고 선점 만료 시각 (PAYMENT_WAITING 상태에서 사용) */
    private Instant holdExpiredAt;

    public ReservationJpaEntity(Long id, String reservationNumber, String reservationRequestId,
                                 Long memberId, Long accommodationId, Long roomOptionId,
                                 ReservationType reservationType, LocalDate checkInDate, LocalDate checkOutDate,
                                 LocalTime hourlyStartTime, int hourlyUsageMinutes,
                                 String guestName, String guestPhone, String guestEmail,
                                 BigDecimal totalPrice, ReservationStatus status, Instant holdExpiredAt) {

        this.id = id;
        this.reservationNumber = reservationNumber;
        this.reservationRequestId = reservationRequestId;
        this.memberId = memberId;
        this.accommodationId = accommodationId;
        this.roomOptionId = roomOptionId;
        this.reservationType = reservationType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.hourlyStartTime = hourlyStartTime;
        this.hourlyUsageMinutes = hourlyUsageMinutes;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.guestEmail = guestEmail;
        this.totalPrice = totalPrice;
        this.status = status;
        this.holdExpiredAt = holdExpiredAt;
    }
}
