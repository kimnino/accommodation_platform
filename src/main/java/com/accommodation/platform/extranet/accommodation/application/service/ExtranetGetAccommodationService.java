package com.accommodation.platform.extranet.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetGetAccommodationQuery;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtranetGetAccommodationService implements ExtranetGetAccommodationQuery {

    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public Accommodation getById(Long accommodationId, Long partnerId) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        if (!accommodation.getPartnerId().equals(partnerId)) {
            throw new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다.");
        }

        return accommodation;
    }

    @Override
    public List<Accommodation> getByPartnerId(Long partnerId) {

        return loadAccommodationPort.findByPartnerId(partnerId);
    }
}
