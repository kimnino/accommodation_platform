package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageJpaRepository extends JpaRepository<RoomImageJpaEntity, Long> {

    List<RoomImageJpaEntity> findByRoomIdOrderByDisplayOrderAsc(Long roomId);

    List<RoomImageJpaEntity> findByRoomIdInOrderByRoomIdAscDisplayOrderAsc(List<Long> roomIds);
}
