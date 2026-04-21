package com.accommodation.platform.core.reservation.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.reservation.domain.model.GuestInfo;
import com.accommodation.platform.core.reservation.domain.model.Reservation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ReservationMapper {

    default Reservation toDomain(ReservationJpaEntity entity) {
        Reservation reservation = Reservation.reconstruct(
                entity.getId(),
                entity.getReservationNumber(),
                entity.getReservationRequestId(),
                entity.getMemberId(),
                entity.getAccommodationId(),
                entity.getRoomOptionId(),
                entity.getReservationType(),
                entity.getCheckInDate(),
                entity.getCheckOutDate(),
                entity.getHourlyStartTime(),
                entity.getHourlyUsageMinutes(),
                new GuestInfo(entity.getGuestName(), entity.getGuestPhone(), entity.getGuestEmail()),
                entity.getTotalPrice(),
                entity.getStatus(),
                entity.getHoldExpiredAt());
        reservation.setCreatedAt(entity.getCreatedAt());
        reservation.setUpdatedAt(entity.getUpdatedAt());
        return reservation;
    }

    @Mapping(source = "guestInfo.name", target = "guestName")
    @Mapping(source = "guestInfo.phone", target = "guestPhone")
    @Mapping(source = "guestInfo.email", target = "guestEmail")
    ReservationJpaEntity toJpaEntity(Reservation domain);

    @AfterMapping
    default void restoreJpaTimestamps(@MappingTarget ReservationJpaEntity entity, Reservation domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }
}
