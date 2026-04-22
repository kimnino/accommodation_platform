package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomOptionTranslationJpaRepository extends JpaRepository<RoomOptionTranslationJpaEntity, Long> {

    List<RoomOptionTranslationJpaEntity> findByRoomOptionId(Long roomOptionId);

    Optional<RoomOptionTranslationJpaEntity> findByRoomOptionIdAndLocale(Long roomOptionId, String locale);

    List<RoomOptionTranslationJpaEntity> findByRoomOptionIdInAndLocale(List<Long> roomOptionIds, String locale);

    void deleteByRoomOptionId(Long roomOptionId);
}
