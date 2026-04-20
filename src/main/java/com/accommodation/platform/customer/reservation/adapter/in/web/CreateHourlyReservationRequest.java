package com.accommodation.platform.customer.reservation.adapter.in.web;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.customer.reservation.application.port.in.CustomerCreateReservationUseCase.CreateHourlyReservationCommand;

public record CreateHourlyReservationRequest(
        @NotBlank(message = "예약 요청 ID는 필수입니다.") String reservationRequestId,
        @NotNull(message = "숙소 ID는 필수입니다.") Long accommodationId,
        @NotNull(message = "객실 옵션 ID는 필수입니다.") Long roomOptionId,
        @NotNull(message = "이용 날짜는 필수입니다.") LocalDate date,
        @NotNull(message = "시작 시간은 필수입니다.") LocalTime startTime,
        @NotBlank(message = "투숙객 이름은 필수입니다.") String guestName,
        @NotBlank(message = "투숙객 연락처는 필수입니다.") String guestPhone,
        String guestEmail
) {

    public CreateHourlyReservationCommand toCommand(Long memberId) {

        return new CreateHourlyReservationCommand(
                reservationRequestId, memberId, accommodationId, roomOptionId,
                date, startTime, guestName, guestPhone, guestEmail);
    }
}
