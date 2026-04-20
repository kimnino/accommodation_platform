package com.accommodation.platform.core.room.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.room.domain.model.Room;

public interface LoadRoomPort {

    Optional<Room> findById(Long id);

    List<Room> findByAccommodationId(Long accommodationId);
}
