package com.accommodation.platform.core.payment.application.port.out;

import com.accommodation.platform.core.payment.domain.model.Payment;

public interface PersistPaymentPort {

    /**
     * 결제를 저장(신규 생성 또는 상태 변경)한다.
     *
     * @param payment 저장할 결제
     * @return 저장된 결제
     */
    Payment save(Payment payment);
}
