package com.accommodation.platform.core.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.payment.domain.enums.PaymentMethod;
import com.accommodation.platform.core.payment.domain.enums.PaymentStatus;

@Getter
public class Payment extends BaseEntity {

    private Long id;
    private final Long reservationId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String pgTransactionId;
    private Instant paidAt;
    private Instant cancelledAt;

    @Builder
    public Payment(Long id, Long reservationId, BigDecimal amount, PaymentMethod paymentMethod) {
        validateRequired(reservationId, amount, paymentMethod);
        this.id = id;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        initTimestamps();
    }

    public void complete(String pgTransactionId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 결제 완료 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.COMPLETED;
        this.pgTransactionId = pgTransactionId;
        this.paidAt = Instant.now();
        updateTimestamp();
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 실패 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.FAILED;
        updateTimestamp();
    }

    public void cancel() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("COMPLETED 상태에서만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = Instant.now();
        updateTimestamp();
    }

    public void refund() {
        if (this.status != PaymentStatus.CANCELLED) {
            throw new IllegalStateException("CANCELLED 상태에서만 환불 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.REFUNDED;
        updateTimestamp();
    }

    public void restoreStatus(PaymentStatus status) {
        this.status = status;
    }

    public void restorePgTransactionId(String pgTransactionId) {
        this.pgTransactionId = pgTransactionId;
    }

    public void restorePaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public void restoreCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    private void validateRequired(Long reservationId, BigDecimal amount, PaymentMethod paymentMethod) {
        if (reservationId == null) {
            throw new IllegalArgumentException("reservationId는 필수입니다.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("결제 수단은 필수입니다.");
        }
    }
}
