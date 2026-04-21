package com.accommodation.platform.extranet.room.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetDeleteRoomUseCase;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetDeleteRoomService implements ExtranetDeleteRoomUseCase {

    private final LoadRoomPort loadRoomPort;
    private final PersistRoomPort persistRoomPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final PersistRoomOptionPort persistRoomOptionPort;
    private final PersistInventoryPort persistInventoryPort;
    private final PersistRoomPricePort persistRoomPricePort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public void delete(Long roomId, Long partnerId) {

        Room room = loadRoomPort.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        // 하위 데이터 고아 방지: 객실에 속한 모든 옵션의 재고/요금을 먼저 삭제한 뒤 옵션, 객실 순으로 삭제
        for (RoomOption option : loadRoomOptionPort.findByRoomId(roomId)) {
            persistInventoryPort.deleteByRoomOptionId(option.getId());
            persistRoomPricePort.deleteByRoomOptionId(option.getId());
            persistRoomOptionPort.delete(option.getId());
        }

        persistRoomPort.delete(roomId);
    }
}
