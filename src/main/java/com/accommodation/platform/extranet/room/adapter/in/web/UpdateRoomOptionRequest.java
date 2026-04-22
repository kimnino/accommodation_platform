package com.accommodation.platform.extranet.room.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomOptionUseCase.UpdateRoomOptionCommand;

public record UpdateRoomOptionRequest(
        String name,
        String cancellationPolicy,
        BigDecimal additionalPrice,
        LocalTime hourlyStartTime,
        LocalTime hourlyEndTime,
        LocalTime checkInTime,
        LocalTime checkOutTime
) {

    public UpdateRoomOptionCommand toCommand() {

        return new UpdateRoomOptionCommand(name, cancellationPolicy, additionalPrice,
                hourlyStartTime, hourlyEndTime, checkInTime, checkOutTime);
    }
}
