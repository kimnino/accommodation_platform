package com.accommodation.platform.extranet.inventory.application.port.in;

public interface ExtranetSetHourlySettingUseCase {

    void setSetting(Long accommodationId, Long partnerId, SetHourlySettingCommand command);

    record SetHourlySettingCommand(
            String operatingStartTime,
            String operatingEndTime,
            int usageDurationMinutes,
            int bufferMinutes,
            int slotUnitMinutes
    ) {
    }
}
