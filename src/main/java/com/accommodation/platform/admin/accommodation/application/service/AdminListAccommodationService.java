package com.accommodation.platform.admin.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminListAccommodationQuery;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminListAccommodationService implements AdminListAccommodationQuery {

    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadAccommodationTranslationPort loadTranslationPort;

    @Override
    public List<Accommodation> listAll() {

        return loadAccommodationPort.findAll();
    }

    @Override
    public List<Accommodation> listByStatus(AccommodationStatus status) {

        return loadAccommodationPort.findByStatus(status);
    }

    @Override
    public Accommodation getById(Long accommodationId) {

        return loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));
    }

    @Override
    public AccommodationDetail getDetailById(Long accommodationId) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));
        List<AccommodationTranslationJpaEntity> translations = loadTranslationPort.findByAccommodationId(accommodationId);
        return new AccommodationDetail(accommodation, translations);
    }
}
