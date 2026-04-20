package com.accommodation.platform.extranet.inventory.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetGetInventoryQuery;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetInventoryUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet/room-options/{roomOptionId}/inventories")
public class ExtranetInventoryController {

    private final ExtranetSetInventoryUseCase setInventoryUseCase;
    private final ExtranetGetInventoryQuery getInventoryQuery;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<InventoryResponse>> setInventory(
            @PathVariable Long roomOptionId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody SetInventoryRequest request) {

        List<Inventory> inventories = setInventoryUseCase.setInventory(
                roomOptionId, partnerId, request.toCommand());

        List<InventoryResponse> responses = inventories.stream()
                .map(InventoryResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping
    public ApiResponse<List<InventoryResponse>> getInventory(
            @PathVariable Long roomOptionId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<InventoryResponse> responses = getInventoryQuery
                .getInventory(roomOptionId, partnerId, startDate, endDate).stream()
                .map(InventoryResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
