package com.accommodation.platform.core.payment.application.port.out;

import java.math.BigDecimal;

import com.accommodation.platform.core.payment.domain.model.PaymentRequest;
import com.accommodation.platform.core.payment.domain.model.PaymentResult;

public interface PaymentGatewayClient {

    /**
     * PG사에 결제를 요청한다.
     *
     * @param request 결제 요청 정보
     * @return 결제 처리 결과
     */
    PaymentResult requestPayment(PaymentRequest request);

    /**
     * PG사에 결제 취소(환불)를 요청한다.
     *
     * @param pgTransactionId PG사 거래 ID
     * @param refundAmount    환불 금액
     * @return 취소 처리 결과
     */
    PaymentResult cancelPayment(String pgTransactionId, BigDecimal refundAmount);
}
