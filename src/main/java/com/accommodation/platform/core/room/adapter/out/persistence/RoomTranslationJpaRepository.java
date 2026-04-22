package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomTranslationJpaRepository extends JpaRepository<RoomTranslationJpaEntity, Long> {

    Optional<RoomTranslationJpaEntity> findByRoomIdAndLocale(Long roomId, String locale);

    List<RoomTranslationJpaEntity> findByRoomIdInAndLocale(List<Long> roomIds, String locale);
}
