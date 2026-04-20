package com.accommodation.platform.core.room.application.port.out;

import com.accommodation.platform.core.room.domain.model.RoomOption;

public interface PersistRoomOptionPort {

    RoomOption save(RoomOption roomOption);

    void delete(Long id);
}
