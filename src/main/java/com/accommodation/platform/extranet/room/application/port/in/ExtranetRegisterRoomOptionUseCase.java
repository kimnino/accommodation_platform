package com.accommodation.platform.extranet.room.application.port.in;

import java.math.BigDecimal;

import com.accommodation.platform.core.room.domain.model.RoomOption;

public interface ExtranetRegisterRoomOptionUseCase {

    RoomOption register(Long roomId, Long partnerId, RegisterRoomOptionCommand command);

    record RegisterRoomOptionCommand(
            String name,
            String cancellationPolicy,
            BigDecimal additionalPrice
    ) {
    }
}
