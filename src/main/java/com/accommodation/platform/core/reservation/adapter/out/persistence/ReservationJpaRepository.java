package com.accommodation.platform.core.reservation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {

    Optional<ReservationJpaEntity> findByReservationRequestId(String reservationRequestId);

    List<ReservationJpaEntity> findByMemberId(Long memberId);

    List<ReservationJpaEntity> findByAccommodationId(Long accommodationId);

    List<ReservationJpaEntity> findByStatus(ReservationStatus status);
}
