package com.accommodation.platform.core.tag.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.tag.application.port.out.LoadAccommodationTagPort;
import com.accommodation.platform.core.tag.application.port.out.PersistAccommodationTagPort;

@Repository
@RequiredArgsConstructor
public class AccommodationTagJpaAdapter implements LoadAccommodationTagPort, PersistAccommodationTagPort {

    private final AccommodationTagJpaRepository jpaRepository;

    @Override
    public List<Long> findTagIdsByAccommodationId(Long accommodationId) {

        return jpaRepository.findByAccommodationId(accommodationId).stream()
                .map(AccommodationTagJpaEntity::getTagId)
                .toList();
    }

    @Override
    public void addTag(Long accommodationId, Long tagId) {

        jpaRepository.save(new AccommodationTagJpaEntity(accommodationId, tagId));
    }

    @Override
    public void removeTags(Long accommodationId, List<Long> tagIds) {

        jpaRepository.deleteByAccommodationIdAndTagIdIn(accommodationId, tagIds);
    }
}
