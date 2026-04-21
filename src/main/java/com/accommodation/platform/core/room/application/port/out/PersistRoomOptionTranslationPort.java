package com.accommodation.platform.core.room.application.port.out;

import java.util.List;

import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionTranslationJpaEntity;

public interface PersistRoomOptionTranslationPort {

    void saveAll(List<RoomOptionTranslationJpaEntity> translations);

    void deleteByRoomOptionId(Long roomOptionId);
}
