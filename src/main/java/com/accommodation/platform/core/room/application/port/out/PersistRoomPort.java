package com.accommodation.platform.core.room.application.port.out;

import com.accommodation.platform.core.room.domain.model.Room;

public interface PersistRoomPort {

    Room save(Room room);

    void delete(Long id);
}
