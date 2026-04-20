package com.accommodation.platform.extranet.room.adapter.in.web;

import java.math.BigDecimal;

import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomOptionUseCase.UpdateRoomOptionCommand;

public record UpdateRoomOptionRequest(
        String name,
        String cancellationPolicy,
        BigDecimal additionalPrice
) {

    public UpdateRoomOptionCommand toCommand() {

        return new UpdateRoomOptionCommand(name, cancellationPolicy, additionalPrice);
    }
}
