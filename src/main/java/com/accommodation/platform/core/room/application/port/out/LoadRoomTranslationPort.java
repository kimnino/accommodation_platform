package com.accommodation.platform.core.room.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.room.adapter.out.persistence.RoomTranslationJpaEntity;

public interface LoadRoomTranslationPort {

    Optional<RoomTranslationJpaEntity> findByRoomIdAndLocale(Long roomId, String locale);

    List<RoomTranslationJpaEntity> findByRoomIdInAndLocale(List<Long> roomIds, String locale);
}
