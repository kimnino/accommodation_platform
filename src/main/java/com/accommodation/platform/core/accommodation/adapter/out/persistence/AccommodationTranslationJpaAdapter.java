package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationTranslation;

@Repository
@RequiredArgsConstructor
public class AccommodationTranslationJpaAdapter implements PersistAccommodationTranslationPort, LoadAccommodationTranslationPort {

    private final AccommodationTranslationJpaRepository jpaRepository;

    @Override
    public void saveAll(List<AccommodationTranslationJpaEntity> translations) {

        jpaRepository.saveAll(translations);
    }

    @Override
    public void deleteByAccommodationId(Long accommodationId) {

        jpaRepository.deleteByAccommodationId(accommodationId);
    }

    @Override
    public List<AccommodationTranslation> findByAccommodationId(Long accommodationId) {

        return jpaRepository.findByAccommodationId(accommodationId).stream()
                .map(this::toRecord)
                .toList();
    }

    @Override
    public Optional<AccommodationTranslation> findByAccommodationIdAndLocale(Long accommodationId, String locale) {

        return jpaRepository.findByAccommodationIdAndLocale(accommodationId, locale)
                .map(this::toRecord);
    }

    @Override
    public List<AccommodationTranslation> findByAccommodationIdInAndLocale(List<Long> accommodationIds, String locale) {

        return jpaRepository.findByAccommodationIdInAndLocale(accommodationIds, locale).stream()
                .map(this::toRecord)
                .toList();
    }

    private AccommodationTranslation toRecord(AccommodationTranslationJpaEntity entity) {

        return new AccommodationTranslation(
                entity.getAccommodationId(),
                entity.getLocale(),
                entity.getName(),
                entity.getFullAddress(),
                entity.getLocationDescription(),
                entity.getIntroduction(),
                entity.getServiceAndFacilities(),
                entity.getUsageInfo(),
                entity.getCancellationAndRefundPolicy());
    }
}
