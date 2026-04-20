package com.accommodation.platform.extranet.room.adapter.in.web;

import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomUseCase.UpdateRoomCommand;

public record UpdateRoomRequest(
        String name,
        String roomTypeName,
        int standardCapacity,
        int maxCapacity
) {

    public UpdateRoomCommand toCommand() {

        return new UpdateRoomCommand(name, roomTypeName, standardCapacity, maxCapacity);
    }
}
