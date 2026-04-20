package com.accommodation.platform.extranet.room.application.port.in;

import java.util.List;

import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;

public interface ExtranetGetRoomQuery {

    List<Room> getRoomsByAccommodationId(Long accommodationId, Long partnerId);

    Room getRoomById(Long roomId, Long partnerId);

    List<RoomOption> getRoomOptionsByRoomId(Long roomId, Long partnerId);
}
