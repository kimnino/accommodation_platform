package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationRegionPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationRegionPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

@Repository
@RequiredArgsConstructor
public class AccommodationRegionJpaAdapter implements LoadAccommodationRegionPort, PersistAccommodationRegionPort {

    private final AccommodationRegionJpaRepository jpaRepository;

    @Override
    public Optional<AccommodationRegion> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AccommodationRegion> findByAccommodationType(AccommodationType type) {
        return jpaRepository.findByAccommodationTypeOrderBySortOrderAsc(type).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AccommodationRegion> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public AccommodationRegion save(AccommodationRegion region) {
        AccommodationRegionJpaEntity entity = region.getId() == null
                ? new AccommodationRegionJpaEntity(region.getAccommodationType(), region.getRegionName(),
                        region.getParentId(), region.getSortOrder())
                : jpaRepository.findById(region.getId()).map(e -> {
                    e.update(region.getRegionName(), region.getParentId(), region.getSortOrder());
                    return e;
                }).orElseThrow();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    private AccommodationRegion toDomain(AccommodationRegionJpaEntity entity) {
        return AccommodationRegion.builder()
                .id(entity.getId())
                .accommodationType(entity.getAccommodationType())
                .regionName(entity.getRegionName())
                .parentId(entity.getParentId())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
