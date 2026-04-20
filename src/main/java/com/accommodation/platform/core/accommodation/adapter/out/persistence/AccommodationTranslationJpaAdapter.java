package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationTranslationPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccommodationTranslationJpaAdapter implements PersistAccommodationTranslationPort {

    private final AccommodationTranslationJpaRepository jpaRepository;

    @Override
    public void saveAll(List<AccommodationTranslationJpaEntity> translations) {

        jpaRepository.saveAll(translations);
    }

    @Override
    public void deleteByAccommodationId(Long accommodationId) {

        jpaRepository.deleteByAccommodationId(accommodationId);
    }
}
