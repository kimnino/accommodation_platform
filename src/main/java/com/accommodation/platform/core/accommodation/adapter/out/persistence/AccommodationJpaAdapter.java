package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

@Repository
@RequiredArgsConstructor
public class AccommodationJpaAdapter implements PersistAccommodationPort, LoadAccommodationPort {

    private final AccommodationJpaRepository jpaRepository;
    private final AccommodationMapper mapper;

    @Override
    public Accommodation save(Accommodation accommodation) {

        AccommodationJpaEntity entity = mapper.toJpaEntity(accommodation);
        AccommodationJpaEntity saved = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Long id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Accommodation> findById(Long id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Accommodation> findByPartnerId(Long partnerId) {

        return jpaRepository.findByPartnerId(partnerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Accommodation> findAll() {

        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
