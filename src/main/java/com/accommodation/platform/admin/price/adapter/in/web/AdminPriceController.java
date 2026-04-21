package com.accommodation.platform.admin.price.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.admin.price.application.port.in.AdminAdjustPriceUseCase;
import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.price.domain.model.RoomPrice;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/room-options")
public class AdminPriceController {

    private final AdminAdjustPriceUseCase adjustPriceUseCase;

    @PatchMapping("/{roomOptionId}/price")
    public ApiResponse<List<RoomPrice>> adjustPrice(
            @PathVariable Long roomOptionId,
            @Valid @RequestBody AdjustPriceRequest request) {

        List<RoomPrice> prices = adjustPriceUseCase.adjustPrice(roomOptionId, request.toCommand());
        return ApiResponse.success(prices);
    }
}
