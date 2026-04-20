package com.accommodation.platform.extranet.room.application.port.in;

public interface ExtranetDeleteRoomUseCase {

    void delete(Long roomId, Long partnerId);
}
