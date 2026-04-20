package com.accommodation.platform.extranet.room.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.room.application.port.out.PersistRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetRegisterRoomService implements ExtranetRegisterRoomUseCase {

    private final PersistRoomPort persistRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public Room register(Long accommodationId, Long partnerId, RegisterRoomCommand command) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        if (!accommodation.getPartnerId().equals(partnerId)) {
            throw new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다.");
        }

        Room room = Room.builder()
                .accommodationId(accommodationId)
                .name(command.name())
                .roomTypeName(command.roomTypeName())
                .standardCapacity(command.standardCapacity())
                .maxCapacity(command.maxCapacity())
                .build();

        return persistRoomPort.save(room);
    }
}
