package com.accommodation.platform.extranet.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationRegionPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetGetAccommodationRegionQuery;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetSetAccommodationRegionUseCase;

@Service
@RequiredArgsConstructor
public class ExtranetAccommodationRegionService implements ExtranetGetAccommodationRegionQuery,
        ExtranetSetAccommodationRegionUseCase {

    private final LoadAccommodationRegionPort loadRegionPort;
    private final LoadAccommodationPort loadAccommodationPort;
    private final PersistAccommodationPort persistAccommodationPort;

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationRegion> getByAccommodationType(AccommodationType type) {

        return loadRegionPort.findByAccommodationType(type);
    }

    @Override
    @Transactional
    public Accommodation setRegion(Long accommodationId, Long partnerId, Long regionId) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        if (!accommodation.getPartnerId().equals(partnerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        AccommodationRegion region = loadRegionPort.findById(regionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND));

        if (region.getAccommodationType() != accommodation.getType()) {
            throw new BusinessException(ErrorCode.REGION_TYPE_MISMATCH);
        }

        accommodation.assignRegion(regionId);
        return persistAccommodationPort.save(accommodation);
    }
}
