package com.accommodation.platform.customer.reservation.adapter.in.web;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase.CreateStayReservationCommand;

public record CreateStayReservationRequest(
        @NotBlank(message = "예약 요청 ID는 필수입니다.") String reservationRequestId,
        @NotNull(message = "숙소 ID는 필수입니다.") Long accommodationId,
        @NotNull(message = "객실 옵션 ID는 필수입니다.") Long roomOptionId,
        @NotNull(message = "체크인 날짜는 필수입니다.") LocalDate checkInDate,
        @NotNull(message = "체크아웃 날짜는 필수입니다.") LocalDate checkOutDate,
        @NotBlank(message = "투숙객 이름은 필수입니다.") String guestName,
        @NotBlank(message = "투숙객 연락처는 필수입니다.") String guestPhone,
        String guestEmail
) {

    public CreateStayReservationCommand toCommand(Long memberId) {

        return new CreateStayReservationCommand(
                reservationRequestId, memberId, accommodationId, roomOptionId,
                checkInDate, checkOutDate, guestName, guestPhone, guestEmail);
    }
}
