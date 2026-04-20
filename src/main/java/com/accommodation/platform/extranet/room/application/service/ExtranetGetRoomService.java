package com.accommodation.platform.extranet.room.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetGetRoomQuery;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtranetGetRoomService implements ExtranetGetRoomQuery {

    private final LoadRoomPort loadRoomPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<Room> getRoomsByAccommodationId(Long accommodationId, Long partnerId) {

        verifyOwnership(accommodationId, partnerId);
        return loadRoomPort.findByAccommodationId(accommodationId);
    }

    @Override
    public Room getRoomById(Long roomId, Long partnerId) {

        Room room = loadRoomPort.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        verifyOwnership(room.getAccommodationId(), partnerId);
        return room;
    }

    @Override
    public List<RoomOption> getRoomOptionsByRoomId(Long roomId, Long partnerId) {

        Room room = loadRoomPort.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        verifyOwnership(room.getAccommodationId(), partnerId);
        return loadRoomOptionPort.findByRoomId(roomId);
    }

    private void verifyOwnership(Long accommodationId, Long partnerId) {

        loadAccommodationPort.findById(accommodationId)
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));
    }
}
