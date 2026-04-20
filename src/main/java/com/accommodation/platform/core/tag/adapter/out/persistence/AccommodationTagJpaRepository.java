package com.accommodation.platform.core.tag.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationTagJpaRepository extends JpaRepository<AccommodationTagJpaEntity, Long> {

    List<AccommodationTagJpaEntity> findByAccommodationId(Long accommodationId);

    void deleteByAccommodationIdAndTagIdIn(Long accommodationId, List<Long> tagIds);
}
