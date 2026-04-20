package com.accommodation.platform.extranet.inventory.application.port.in;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

public interface ExtranetSetTimeSlotInventoryUseCase {

    List<TimeSlotInventory> openTimeSlots(Long roomOptionId, Long partnerId, OpenTimeSlotsCommand command);

    record OpenTimeSlotsCommand(
            LocalDate startDate,
            LocalDate endDate,
            int totalQuantity
    ) {
    }
}
