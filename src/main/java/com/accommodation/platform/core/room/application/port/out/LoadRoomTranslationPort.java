package com.accommodation.platform.core.room.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.room.domain.model.RoomTranslation;

public interface LoadRoomTranslationPort {

    Optional<RoomTranslation> findByRoomIdAndLocale(Long roomId, String locale);

    List<RoomTranslation> findByRoomIdInAndLocale(List<Long> roomIds, String locale);
}
