package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.accommodation.application.port.out.LoadHourlySettingPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistHourlySettingPort;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationHourlySetting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccommodationHourlySettingJpaAdapter implements LoadHourlySettingPort, PersistHourlySettingPort {

    private final AccommodationHourlySettingJpaRepository repository;

    @Override
    public Optional<AccommodationHourlySetting> findByAccommodationId(Long accommodationId) {
        return repository.findByAccommodationId(accommodationId)
                .map(this::toDomain);
    }

    @Override
    public AccommodationHourlySetting save(AccommodationHourlySetting setting) {
        AccommodationHourlySettingJpaEntity entity = new AccommodationHourlySettingJpaEntity(
                setting.getAccommodationId(),
                setting.getOperatingStartTime(),
                setting.getOperatingEndTime(),
                setting.getUsageDurationMinutes(),
                setting.getBufferMinutes(),
                setting.getSlotUnitMinutes()
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public void deleteByAccommodationId(Long accommodationId) {
        repository.findByAccommodationId(accommodationId)
                .ifPresent(repository::delete);
    }

    private AccommodationHourlySetting toDomain(AccommodationHourlySettingJpaEntity entity) {
        return new AccommodationHourlySetting(
                entity.getId(),
                entity.getAccommodationId(),
                entity.getOperatingStartTime(),
                entity.getOperatingEndTime(),
                entity.getUsageDurationMinutes(),
                entity.getBufferMinutes(),
                entity.getSlotUnitMinutes()
        );
    }
}
