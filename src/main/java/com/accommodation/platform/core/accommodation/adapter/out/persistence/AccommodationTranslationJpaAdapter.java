package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationTranslationPort;

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
    public List<AccommodationTranslationJpaEntity> findByAccommodationId(Long accommodationId) {

        return jpaRepository.findByAccommodationId(accommodationId);
    }

    @Override
    public Optional<AccommodationTranslationJpaEntity> findByAccommodationIdAndLocale(Long accommodationId, String locale) {

        return jpaRepository.findByAccommodationIdAndLocale(accommodationId, locale);
    }

    @Override
    public List<AccommodationTranslationJpaEntity> findByAccommodationIdInAndLocale(List<Long> accommodationIds, String locale) {

        return jpaRepository.findByAccommodationIdInAndLocale(accommodationIds, locale);
    }
}
