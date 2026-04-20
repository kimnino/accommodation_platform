package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoomJpaAdapter implements PersistRoomPort, LoadRoomPort {

    private final RoomJpaRepository jpaRepository;
    private final RoomMapper mapper;

    @Override
    public Room save(Room room) {

        RoomJpaEntity entity = mapper.toJpaEntity(room);
        RoomJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Long id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Room> findById(Long id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Room> findByAccommodationId(Long accommodationId) {

        return jpaRepository.findByAccommodationId(accommodationId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
