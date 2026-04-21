package com.accommodation.platform.core.payment.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    Optional<PaymentJpaEntity> findByReservationIdAndIsDeletedFalse(Long reservationId);

    Optional<PaymentJpaEntity> findByIdAndIsDeletedFalse(Long id);
}
