package com.accommodation.platform.core.payment.adapter.out.persistence;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.payment.application.port.out.LoadPaymentPort;
import com.accommodation.platform.core.payment.application.port.out.PersistPaymentPort;
import com.accommodation.platform.core.payment.domain.model.Payment;

@Component
@RequiredArgsConstructor
public class PaymentJpaAdapter implements LoadPaymentPort, PersistPaymentPort {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Optional<Payment> findById(Long id) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        // TODO: implement
        return Optional.empty();
    }

    @Override
    public Payment save(Payment payment) {
        // TODO: implement
        return payment;
    }
}
