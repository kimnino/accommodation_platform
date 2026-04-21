package com.accommodation.platform.core.payment.domain.enums;

public enum PaymentStatus {

    /** 결제 대기 */
    PENDING,

    /** 결제 완료 */
    COMPLETED,

    /** 결제 실패 */
    FAILED,

    /** 결제 취소 */
    CANCELLED,

    /** 환불 완료 */
    REFUNDED
}
