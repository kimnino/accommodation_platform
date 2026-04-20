package com.accommodation.platform.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "잘못된 입력값입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "입력값을 확인해주세요."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),

    // Accommodation
    ACCOMMODATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOMMODATION_NOT_FOUND", "숙소를 찾을 수 없습니다."),

    // Room
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND", "객실을 찾을 수 없습니다."),

    // Inventory
    INVENTORY_NOT_AVAILABLE(HttpStatus.CONFLICT, "INVENTORY_NOT_AVAILABLE", "선택하신 날짜에 예약 가능한 객실이 없습니다."),

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
