package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomOptionJpaRepository extends JpaRepository<RoomOptionJpaEntity, Long> {

    List<RoomOptionJpaEntity> findByRoomId(Long roomId);
}
