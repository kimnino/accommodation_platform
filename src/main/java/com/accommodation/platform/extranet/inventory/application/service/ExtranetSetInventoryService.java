package com.accommodation.platform.extranet.inventory.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetInventoryUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetSetInventoryService implements ExtranetSetInventoryUseCase {

    private final PersistInventoryPort persistInventoryPort;
    private final LoadInventoryPort loadInventoryPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<Inventory> setInventory(Long roomOptionId, Long partnerId, SetInventoryCommand command) {

        RoomOption option = loadRoomOptionPort.findById(roomOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND, "객실 옵션을 찾을 수 없습니다."));

        Room room = loadRoomPort.findById(option.getRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        List<Inventory> inventories = new ArrayList<>();
        for (LocalDate date : command.getTargetDates()) {

            List<Inventory> existing = loadInventoryPort.findByRoomOptionIdAndDateRange(roomOptionId, date, date);

            if (!existing.isEmpty()) {
                Inventory inv = existing.getFirst();
                inv.updateTotalQuantity(command.totalQuantity());
                inventories.add(inv);
            } else {
                Inventory inv = Inventory.builder()
                        .roomOptionId(roomOptionId)
                        .date(date)
                        .totalQuantity(command.totalQuantity())
                        .remainingQuantity(command.totalQuantity())
                        .build();
                inventories.add(inv);
            }
        }

        return persistInventoryPort.saveAll(inventories);
    }
}
