package com.accommodation.platform.core.room.application.port.out;

import java.util.List;

import com.accommodation.platform.core.room.domain.model.RoomImage;

public interface LoadRoomImagePort {

    List<RoomImage> findByRoomId(Long roomId);

    List<RoomImage> findByRoomIdIn(List<Long> roomIds);
}
