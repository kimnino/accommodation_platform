package com.accommodation.platform.core.payment.domain.model;

/**
 * PG사 결제 처리 결과.
 */
public record PaymentResult(

    /** 성공 여부 */
    boolean success,

    /** PG사 거래 ID (성공 시 존재) */
    String pgTransactionId,

    /** 오류 코드 (실패 시 존재) */
    String errorCode,

    /** 오류 메시지 (실패 시 존재) */
    String errorMessage
) {

    public static PaymentResult success(String pgTransactionId) {
        return new PaymentResult(true, pgTransactionId, null, null);
    }

    public static PaymentResult failure(String errorCode, String errorMessage) {
        return new PaymentResult(false, null, errorCode, errorMessage);
    }
}
