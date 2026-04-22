package com.accommodation.platform.extranet.inventory.application.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadHourlySettingPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationHourlySetting;
import com.accommodation.platform.core.inventory.application.port.out.PersistInventoryPort;
import com.accommodation.platform.core.inventory.domain.enums.TimeSlotStatus;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetTimeSlotInventoryUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetSetTimeSlotInventoryService implements ExtranetSetTimeSlotInventoryUseCase {

    private final PersistInventoryPort persistInventoryPort;
    private final LoadHourlySettingPort loadHourlySettingPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<TimeSlotInventory> openTimeSlots(Long roomOptionId, Long partnerId,
                                                  OpenTimeSlotsCommand command) {

        RoomOption option = loadRoomOptionPort.findById(roomOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND, "객실 옵션을 찾을 수 없습니다."));

        Room room = loadRoomPort.findById(option.getRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        Accommodation accommodation = loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        AccommodationHourlySetting setting = loadHourlySettingPort.findByAccommodationId(accommodation.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT, "대실 운영 설정이 필요합니다."));

        List<TimeSlotInventory> result = new ArrayList<>();

        for (LocalDate date = command.startDate(); !date.isAfter(command.endDate()); date = date.plusDays(1)) {
            List<TimeSlotInventory> slots = generateSlots(
                    roomOptionId, date, setting.getOperatingStartTime(),
                    setting.getOperatingEndTime(), setting.getSlotUnitMinutes());
            result.addAll(persistInventoryPort.saveAllTimeSlots(slots));
        }

        return result;
    }

    private List<TimeSlotInventory> generateSlots(Long roomOptionId, LocalDate date,
                                                    LocalTime operatingStart, LocalTime operatingEnd,
                                                    int slotUnitMinutes) {

        List<TimeSlotInventory> slots = new ArrayList<>();
        LocalTime current = operatingStart;

        while (current.isBefore(operatingEnd)) {
            slots.add(TimeSlotInventory.builder()
                    .roomOptionId(roomOptionId)
                    .date(date)
                    .slotTime(current)
                    .status(TimeSlotStatus.AVAILABLE)
                    .build());
            current = current.plusMinutes(slotUnitMinutes);
        }

        return slots;
    }
}
