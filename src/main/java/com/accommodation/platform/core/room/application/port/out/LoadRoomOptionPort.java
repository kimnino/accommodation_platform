package com.accommodation.platform.core.room.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.room.domain.model.RoomOption;

public interface LoadRoomOptionPort {

    Optional<RoomOption> findById(Long id);

    List<RoomOption> findByRoomId(Long roomId);
}
