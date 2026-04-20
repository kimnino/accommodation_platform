package com.accommodation.platform.admin.accommodation.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminApproveAccommodationUseCase;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminApproveAccommodationService implements AdminApproveAccommodationUseCase {

    private final PersistAccommodationPort persistAccommodationPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public Accommodation approve(Long accommodationId) {

        Accommodation accommodation = findAccommodation(accommodationId);
        accommodation.activate();
        return persistAccommodationPort.save(accommodation);
    }

    @Override
    public Accommodation suspend(Long accommodationId) {

        Accommodation accommodation = findAccommodation(accommodationId);
        accommodation.suspend();
        return persistAccommodationPort.save(accommodation);
    }

    @Override
    public Accommodation close(Long accommodationId) {

        Accommodation accommodation = findAccommodation(accommodationId);
        accommodation.close();
        return persistAccommodationPort.save(accommodation);
    }

    private Accommodation findAccommodation(Long accommodationId) {

        return loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));
    }
}
