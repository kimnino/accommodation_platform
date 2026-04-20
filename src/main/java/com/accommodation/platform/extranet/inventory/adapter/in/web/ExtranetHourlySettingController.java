package com.accommodation.platform.extranet.inventory.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetHourlySettingUseCase;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetTimeSlotInventoryUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet")
public class ExtranetHourlySettingController {

    private final ExtranetSetHourlySettingUseCase setHourlySettingUseCase;
    private final ExtranetSetTimeSlotInventoryUseCase setTimeSlotInventoryUseCase;

    @PutMapping("/accommodations/{accommodationId}/hourly-setting")
    public ApiResponse<Void> setHourlySetting(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody SetHourlySettingRequest request) {

        setHourlySettingUseCase.setSetting(accommodationId, partnerId, request.toCommand());
        return ApiResponse.success(null);
    }

    @PostMapping("/room-options/{roomOptionId}/time-slots")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<TimeSlotResponse>> openTimeSlots(
            @PathVariable Long roomOptionId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody OpenTimeSlotsRequest request) {

        List<TimeSlotInventory> slots = setTimeSlotInventoryUseCase.openTimeSlots(
                roomOptionId, partnerId, request.toCommand());

        List<TimeSlotResponse> responses = slots.stream()
                .map(TimeSlotResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
