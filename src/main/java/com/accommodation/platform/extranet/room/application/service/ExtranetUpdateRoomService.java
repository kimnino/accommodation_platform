package com.accommodation.platform.extranet.room.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetUpdateRoomService implements ExtranetUpdateRoomUseCase {

    private final LoadRoomPort loadRoomPort;
    private final PersistRoomPort persistRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public Room update(Long roomId, Long partnerId, UpdateRoomCommand command) {

        Room room = loadRoomPort.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        verifyOwnership(room.getAccommodationId(), partnerId);

        room.updateInfo(
                command.name(),
                command.roomTypeName(),
                command.standardCapacity(),
                command.maxCapacity());

        return persistRoomPort.save(room);
    }

    private void verifyOwnership(Long accommodationId, Long partnerId) {

        loadAccommodationPort.findById(accommodationId)
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));
    }
}
