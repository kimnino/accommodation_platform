package com.accommodation.platform.core.reservation.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.reservation.domain.model.GuestInfo;
import com.accommodation.platform.core.reservation.domain.model.Reservation;

@Component
public class ReservationMapper {

    public Reservation toDomain(ReservationJpaEntity entity) {

        Reservation reservation = Reservation.builder()
                .id(entity.getId())
                .reservationRequestId(entity.getReservationRequestId())
                .memberId(entity.getMemberId())
                .accommodationId(entity.getAccommodationId())
                .roomOptionId(entity.getRoomOptionId())
                .reservationType(entity.getReservationType())
                .checkInDate(entity.getCheckInDate())
                .checkOutDate(entity.getCheckOutDate())
                .hourlyStartTime(entity.getHourlyStartTime())
                .hourlyUsageMinutes(entity.getHourlyUsageMinutes())
                .guestInfo(new GuestInfo(entity.getGuestName(), entity.getGuestPhone(), entity.getGuestEmail()))
                .totalPrice(entity.getTotalPrice())
                .build();

        reservation.setCreatedAt(entity.getCreatedAt());
        reservation.setUpdatedAt(entity.getUpdatedAt());

        return reservation;
    }

    public ReservationJpaEntity toJpaEntity(Reservation domain) {

        return new ReservationJpaEntity(
                domain.getId(),
                domain.getReservationNumber(),
                domain.getReservationRequestId(),
                domain.getMemberId(),
                domain.getAccommodationId(),
                domain.getRoomOptionId(),
                domain.getReservationType(),
                domain.getCheckInDate(),
                domain.getCheckOutDate(),
                domain.getHourlyStartTime(),
                domain.getHourlyUsageMinutes(),
                domain.getGuestInfo().name(),
                domain.getGuestInfo().phone(),
                domain.getGuestInfo().email(),
                domain.getTotalPrice(),
                domain.getStatus(),
                domain.getHoldExpiredAt());
    }
}
