package com.accommodation.platform.core.reservation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.application.port.out.PersistReservationPort;
import com.accommodation.platform.core.reservation.domain.enums.ReservationStatus;
import com.accommodation.platform.core.reservation.domain.model.Reservation;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationJpaAdapter implements PersistReservationPort, LoadReservationPort {

    private final ReservationJpaRepository jpaRepository;
    private final ReservationMapper mapper;

    @Override
    public Reservation save(Reservation reservation) {

        ReservationJpaEntity entity = mapper.toJpaEntity(reservation);
        ReservationJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Reservation> findById(Long id) {

        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Reservation> findByReservationRequestId(String reservationRequestId) {

        return jpaRepository.findByReservationRequestId(reservationRequestId).map(mapper::toDomain);
    }

    @Override
    public List<Reservation> findByMemberId(Long memberId) {

        return jpaRepository.findByMemberId(memberId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findByAccommodationId(Long accommodationId) {

        return jpaRepository.findByAccommodationId(accommodationId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {

        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }
}
