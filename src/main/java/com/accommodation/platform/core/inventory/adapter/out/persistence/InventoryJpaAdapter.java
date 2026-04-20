package com.accommodation.platform.core.inventory.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InventoryJpaAdapter implements PersistInventoryPort, LoadInventoryPort {

    private final InventoryJpaRepository jpaRepository;
    private final InventoryMapper mapper;

    @Override
    public Inventory save(Inventory inventory) {

        InventoryJpaEntity entity = mapper.toJpaEntity(inventory);
        InventoryJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Inventory> saveAll(List<Inventory> inventories) {

        List<InventoryJpaEntity> entities = inventories.stream()
                .map(mapper::toJpaEntity)
                .toList();
        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Inventory> findById(Long id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Inventory> findByRoomOptionIdAndDateRange(Long roomOptionId,
                                                          LocalDate startDate, LocalDate endDate) {

        return jpaRepository.findByRoomOptionIdAndDateBetweenOrderByDateAsc(roomOptionId, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Inventory> findWithLock(Long roomOptionId, LocalDate date) {

        return jpaRepository.findWithLock(roomOptionId, date)
                .map(mapper::toDomain);
    }
}
