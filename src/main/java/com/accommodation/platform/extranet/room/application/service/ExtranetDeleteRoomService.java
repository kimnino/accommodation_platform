package com.accommodation.platform.extranet.room.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetDeleteRoomUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetDeleteRoomService implements ExtranetDeleteRoomUseCase {

    private final LoadRoomPort loadRoomPort;
    private final PersistRoomPort persistRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public void delete(Long roomId, Long partnerId) {

        Room room = loadRoomPort.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        persistRoomPort.delete(roomId);
    }
}
