package com.accommodation.platform.extranet.room.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomOptionPort;
import com.accommodation.platform.core.room.domain.enums.CancellationPolicy;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetUpdateRoomOptionUseCase;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetUpdateRoomOptionService implements ExtranetUpdateRoomOptionUseCase {

    private final LoadRoomOptionPort loadRoomOptionPort;
    private final PersistRoomOptionPort persistRoomOptionPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public RoomOption update(Long roomOptionId, Long partnerId, UpdateRoomOptionCommand command) {

        RoomOption option = loadRoomOptionPort.findById(roomOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND, "객실 옵션을 찾을 수 없습니다."));

        Room room = loadRoomPort.findById(option.getRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        option.updateInfo(
                command.name(),
                command.cancellationPolicy() != null ? CancellationPolicy.valueOf(command.cancellationPolicy()) : null,
                command.additionalPrice());

        return persistRoomOptionPort.save(option);
    }
}
