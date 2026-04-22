package com.accommodation.platform.extranet.room.application.port.in;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.accommodation.platform.core.room.domain.model.RoomOption;

public interface ExtranetUpdateRoomOptionUseCase {

    RoomOption update(Long roomOptionId, Long partnerId, UpdateRoomOptionCommand command);

    record UpdateRoomOptionCommand(
            String name,
            String cancellationPolicy,
            BigDecimal additionalPrice,
            LocalTime hourlyStartTime,
            LocalTime hourlyEndTime,
            LocalTime checkInTime,
            LocalTime checkOutTime
    ) {
    }
}
