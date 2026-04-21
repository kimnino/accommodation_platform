package com.accommodation.platform.core.payment.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.payment.domain.enums.PaymentMethod;
import com.accommodation.platform.core.payment.domain.enums.PaymentStatus;

import static lombok.AccessLevel.PROTECTED;

/**
 * 결제 테이블.
 * 예약별 결제 정보와 PG사 거래 내역을 관리한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "payment",
    indexes = {
        @Index(name = "idx_payment_reservation_id", columnList = "reservationId")
    }
)
public class PaymentJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 예약 ID (reservation 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long reservationId;

    /**
     * 결제 금액
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * 결제 수단 (CARD / VIRTUAL_ACCOUNT / KAKAO_PAY / NAVER_PAY)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * 결제 상태 (PENDING / COMPLETED / FAILED / CANCELLED / REFUNDED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * PG사 거래 ID (결제 완료 시 발급)
     */
    private String pgTransactionId;

    /**
     * 결제 완료 일시 (UTC)
     */
    private Instant paidAt;

    /**
     * 결제 취소 일시 (UTC)
     */
    private Instant cancelledAt;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    public PaymentJpaEntity(Long id, Long reservationId, BigDecimal amount,
                            PaymentMethod paymentMethod, PaymentStatus status,
                            String pgTransactionId, Instant paidAt, Instant cancelledAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.pgTransactionId = pgTransactionId;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
    }

    public void update(PaymentStatus status, String pgTransactionId, Instant paidAt, Instant cancelledAt) {
        this.status = status;
        this.pgTransactionId = pgTransactionId;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
