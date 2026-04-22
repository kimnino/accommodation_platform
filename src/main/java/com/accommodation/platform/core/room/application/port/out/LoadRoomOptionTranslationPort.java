package com.accommodation.platform.core.room.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.room.domain.model.RoomOptionTranslation;

public interface LoadRoomOptionTranslationPort {

    Optional<RoomOptionTranslation> findByRoomOptionIdAndLocale(Long roomOptionId, String locale);

    List<RoomOptionTranslation> findByRoomOptionIdInAndLocale(List<Long> roomOptionIds, String locale);
}
