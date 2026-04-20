package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<RoomJpaEntity, Long> {

    List<RoomJpaEntity> findByAccommodationId(Long accommodationId);
}
