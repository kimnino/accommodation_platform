package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomOptionPort;
import com.accommodation.platform.core.room.domain.model.RoomOption;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoomOptionJpaAdapter implements PersistRoomOptionPort, LoadRoomOptionPort {

    private final RoomOptionJpaRepository jpaRepository;
    private final RoomMapper mapper;

    @Override
    public RoomOption save(RoomOption roomOption) {

        RoomOptionJpaEntity entity = mapper.toJpaEntity(roomOption);
        RoomOptionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Long id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<RoomOption> findById(Long id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<RoomOption> findByRoomId(Long roomId) {

        return jpaRepository.findByRoomId(roomId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
