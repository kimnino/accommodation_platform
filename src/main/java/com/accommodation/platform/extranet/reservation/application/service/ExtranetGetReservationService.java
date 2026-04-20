package com.accommodation.platform.extranet.reservation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.reservation.application.port.out.LoadReservationPort;
import com.accommodation.platform.core.reservation.domain.model.Reservation;
import com.accommodation.platform.extranet.reservation.application.port.in.ExtranetGetReservationQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtranetGetReservationService implements ExtranetGetReservationQuery {

    private final LoadReservationPort loadReservationPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<Reservation> getByAccommodationId(Long accommodationId, Long partnerId) {

        loadAccommodationPort.findById(accommodationId)
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        return loadReservationPort.findByAccommodationId(accommodationId);
    }
}
