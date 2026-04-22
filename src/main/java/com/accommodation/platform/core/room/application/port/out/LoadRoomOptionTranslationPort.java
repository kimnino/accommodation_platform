package com.accommodation.platform.core.room.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionTranslationJpaEntity;

public interface LoadRoomOptionTranslationPort {

    Optional<RoomOptionTranslationJpaEntity> findByRoomOptionIdAndLocale(Long roomOptionId, String locale);

    List<RoomOptionTranslationJpaEntity> findByRoomOptionIdInAndLocale(List<Long> roomOptionIds, String locale);
}
