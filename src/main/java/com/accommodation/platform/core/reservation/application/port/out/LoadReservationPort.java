package com.accommodation.platform.core.reservation.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.model.Reservation;

public interface LoadReservationPort {

    Optional<Reservation> findById(Long id);

    Optional<Reservation> findByReservationRequestId(String reservationRequestId);

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByAccommodationId(Long accommodationId);

    List<Reservation> findByStatus(ReservationStatus status);
}
