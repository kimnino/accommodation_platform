package com.accommodation.platform.extranet.room.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetGetRoomQuery;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomOptionUseCase;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet/accommodations/{accommodationId}/rooms")
public class ExtranetRoomController {

    private final ExtranetRegisterRoomUseCase registerRoomUseCase;
    private final ExtranetRegisterRoomOptionUseCase registerRoomOptionUseCase;
    private final ExtranetGetRoomQuery getRoomQuery;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoomDetailResponse> registerRoom(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody RegisterRoomRequest request) {

        Room room = registerRoomUseCase.register(accommodationId, partnerId, request.toCommand());
        return ApiResponse.success(RoomDetailResponse.from(room));
    }

    @GetMapping
    public ApiResponse<List<RoomDetailResponse>> getRooms(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        List<RoomDetailResponse> responses = getRoomQuery.getRoomsByAccommodationId(accommodationId, partnerId)
                .stream()
                .map(RoomDetailResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{roomId}")
    public ApiResponse<RoomDetailResponse> getRoom(
            @PathVariable Long accommodationId,
            @PathVariable Long roomId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        Room room = getRoomQuery.getRoomById(roomId, partnerId);
        return ApiResponse.success(RoomDetailResponse.from(room));
    }

    @PostMapping("/{roomId}/options")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoomOptionResponse> registerRoomOption(
            @PathVariable Long accommodationId,
            @PathVariable Long roomId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody RegisterRoomOptionRequest request) {

        RoomOption option = registerRoomOptionUseCase.register(roomId, partnerId, request.toCommand());
        return ApiResponse.success(RoomOptionResponse.from(option));
    }

    @GetMapping("/{roomId}/options")
    public ApiResponse<List<RoomOptionResponse>> getRoomOptions(
            @PathVariable Long accommodationId,
            @PathVariable Long roomId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        List<RoomOptionResponse> responses = getRoomQuery.getRoomOptionsByRoomId(roomId, partnerId)
                .stream()
                .map(RoomOptionResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
