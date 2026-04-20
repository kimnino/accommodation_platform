package com.accommodation.platform.extranet.accommodation.application.service;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetRegisterAccommodationService implements ExtranetRegisterAccommodationUseCase {

    private final PersistAccommodationPort persistAccommodationPort;

    @Override
    public Accommodation register(RegisterAccommodationCommand command) {

        Accommodation accommodation = Accommodation.builder()
                .partnerId(command.partnerId())
                .name(command.name())
                .type(AccommodationType.valueOf(command.type()))
                .fullAddress(command.fullAddress())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .locationDescription(command.locationDescription())
                .checkInTime(command.checkInTime() != null ? LocalTime.parse(command.checkInTime()) : null)
                .checkOutTime(command.checkOutTime() != null ? LocalTime.parse(command.checkOutTime()) : null)
                .build();

        return persistAccommodationPort.save(accommodation);
    }
}
