package com.accommodation.platform.core.accommodation.domain.model;

import java.time.LocalTime;

public class AccommodationHourlySetting {

    private final Long id;
    private final Long accommodationId;
    private final LocalTime operatingStartTime;
    private final LocalTime operatingEndTime;
    private final int usageDurationMinutes;
    private final int bufferMinutes;
    private final int slotUnitMinutes;

    public AccommodationHourlySetting(Long id, Long accommodationId,
                                       LocalTime operatingStartTime, LocalTime operatingEndTime,
                                       int usageDurationMinutes, int bufferMinutes, int slotUnitMinutes) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.usageDurationMinutes = usageDurationMinutes;
        this.bufferMinutes = bufferMinutes;
        this.slotUnitMinutes = slotUnitMinutes;
    }

    public Long getId() { return id; }
    public Long getAccommodationId() { return accommodationId; }
    public LocalTime getOperatingStartTime() { return operatingStartTime; }
    public LocalTime getOperatingEndTime() { return operatingEndTime; }
    public int getUsageDurationMinutes() { return usageDurationMinutes; }
    public int getBufferMinutes() { return bufferMinutes; }
    public int getSlotUnitMinutes() { return slotUnitMinutes; }
}
