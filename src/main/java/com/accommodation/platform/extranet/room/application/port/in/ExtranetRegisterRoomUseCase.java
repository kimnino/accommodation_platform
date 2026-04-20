package com.accommodation.platform.extranet.room.application.port.in;

import com.accommodation.platform.core.room.domain.model.Room;

public interface ExtranetRegisterRoomUseCase {

    Room register(Long accommodationId, Long partnerId, RegisterRoomCommand command);

    record RegisterRoomCommand(
            String name,
            String roomTypeName,
            int standardCapacity,
            int maxCapacity
    ) {
    }
}
