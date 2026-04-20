package com.accommodation.platform.extranet.inventory.adapter.in.web;

import java.time.LocalDate;
import java.time.LocalTime;

import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

public record TimeSlotResponse(
        Long id,
        Long roomOptionId,
        LocalDate date,
        LocalTime slotTime,
        String status
) {

    public static TimeSlotResponse from(TimeSlotInventory slot) {

        return new TimeSlotResponse(
                slot.getId(),
                slot.getRoomOptionId(),
                slot.getDate(),
                slot.getSlotTime(),
                slot.getStatus().name());
    }
}
