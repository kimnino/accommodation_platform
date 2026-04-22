package com.accommodation.platform.customer.accommodation.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetRoomsQuery;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetRoomsQuery.RoomDetail;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accommodations")
public class CustomerRoomController {

    private final CustomerGetRoomsQuery getRoomsQuery;

    @GetMapping("/{accommodationId}/rooms")
    public ApiResponse<List<RoomDetail>> getRooms(
            @PathVariable Long accommodationId,
            @RequestParam(name = "check_in_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(name = "check_out_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {

        List<RoomDetail> rooms = getRoomsQuery.getRooms(accommodationId, checkInDate, checkOutDate);
        return ApiResponse.success(rooms);
    }
}
