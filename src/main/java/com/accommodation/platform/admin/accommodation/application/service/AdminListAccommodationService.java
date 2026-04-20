package com.accommodation.platform.admin.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminListAccommodationQuery;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminListAccommodationService implements AdminListAccommodationQuery {

    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<Accommodation> listAll() {

        return loadAccommodationPort.findAll();
    }

    @Override
    public Accommodation getById(Long accommodationId) {

        return loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));
    }
}
