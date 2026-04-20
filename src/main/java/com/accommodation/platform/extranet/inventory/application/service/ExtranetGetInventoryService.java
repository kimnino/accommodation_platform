package com.accommodation.platform.extranet.inventory.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetGetInventoryQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtranetGetInventoryService implements ExtranetGetInventoryQuery {

    private final LoadInventoryPort loadInventoryPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<Inventory> getInventory(Long roomOptionId, Long partnerId,
                                         LocalDate startDate, LocalDate endDate) {

        RoomOption option = loadRoomOptionPort.findById(roomOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND, "객실 옵션을 찾을 수 없습니다."));

        Room room = loadRoomPort.findById(option.getRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        return loadInventoryPort.findByRoomOptionIdAndDateRange(roomOptionId, startDate, endDate);
    }
}
