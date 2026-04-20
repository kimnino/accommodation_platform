package com.accommodation.platform.extranet.price.adapter.in.web;

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
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.extranet.price.application.port.in.ExtranetGetPriceQuery;
import com.accommodation.platform.extranet.price.application.port.in.ExtranetSetPriceUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet/room-options/{roomOptionId}/prices")
public class ExtranetPriceController {

    private final ExtranetSetPriceUseCase setPriceUseCase;
    private final ExtranetGetPriceQuery getPriceQuery;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<PriceResponse>> setPrice(
            @PathVariable Long roomOptionId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @Valid @RequestBody SetPriceRequest request) {

        List<RoomPrice> prices = setPriceUseCase.setPrice(roomOptionId, partnerId, request.toCommand());

        List<PriceResponse> responses = prices.stream()
                .map(PriceResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping
    public ApiResponse<List<PriceResponse>> getPrice(
            @PathVariable Long roomOptionId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PriceResponse> responses = getPriceQuery
                .getPrice(roomOptionId, partnerId, startDate, endDate).stream()
                .map(PriceResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
