package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.room.application.port.out.LoadRoomImagePort;
import com.accommodation.platform.core.room.domain.model.RoomImage;

@Repository
@RequiredArgsConstructor
public class RoomImageJpaAdapter implements LoadRoomImagePort {

    private final RoomImageJpaRepository jpaRepository;

    @Override
    public List<RoomImage> findByRoomId(Long roomId) {

        return jpaRepository.findByRoomIdOrderByDisplayOrderAsc(roomId).stream()
                .map(this::toRecord)
                .toList();
    }

    @Override
    public List<RoomImage> findByRoomIdIn(List<Long> roomIds) {

        return jpaRepository.findByRoomIdInOrderByRoomIdAscDisplayOrderAsc(roomIds).stream()
                .map(this::toRecord)
                .toList();
    }

    private RoomImage toRecord(RoomImageJpaEntity entity) {

        return new RoomImage(
                entity.getRoomId(),
                entity.getRelativePath(),
                entity.getDisplayOrder(),
                entity.isPrimary());
    }
}
