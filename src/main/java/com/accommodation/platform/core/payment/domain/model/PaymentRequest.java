package com.accommodation.platform.core.payment.domain.model;

import java.math.BigDecimal;

import com.accommodation.platform.core.payment.domain.enums.PaymentMethod;

/**
 * PG사 결제 요청 데이터.
 */
public record PaymentRequest(

    /** 예약 ID */
    Long reservationId,

    /** 결제 금액 */
    BigDecimal amount,

    /** 결제 수단 */
    PaymentMethod paymentMethod,

    /** 주문 ID (PG사 식별용 고유값) */
    String orderId
) {}
