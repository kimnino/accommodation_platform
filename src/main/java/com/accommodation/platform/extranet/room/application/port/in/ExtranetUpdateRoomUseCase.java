package com.accommodation.platform.extranet.room.application.port.in;

import com.accommodation.platform.core.room.domain.model.Room;

public interface ExtranetUpdateRoomUseCase {

    Room update(Long roomId, Long partnerId, UpdateRoomCommand command);

    record UpdateRoomCommand(
            String name,
            String roomTypeName,
            int standardCapacity,
            int maxCapacity
    ) {
    }
}
