package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomOptionTranslationJpaRepository extends JpaRepository<RoomOptionTranslationJpaEntity, Long> {

    List<RoomOptionTranslationJpaEntity> findByRoomOptionId(Long roomOptionId);

    void deleteByRoomOptionId(Long roomOptionId);
}
