package com.accommodation.platform.admin.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminGetAccommodationRegionQuery;
import com.accommodation.platform.admin.accommodation.application.port.in.AdminManageAccommodationRegionUseCase;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationRegionPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationRegionPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

@Service
@RequiredArgsConstructor
public class AdminManageAccommodationRegionService implements AdminManageAccommodationRegionUseCase,
        AdminGetAccommodationRegionQuery {

    private final PersistAccommodationRegionPort persistRegionPort;
    private final LoadAccommodationRegionPort loadRegionPort;

    @Override
    @Transactional
    public AccommodationRegion create(CreateRegionCommand command) {

        AccommodationRegion region = AccommodationRegion.builder()
                .accommodationType(command.accommodationType())
                .regionName(command.regionName())
                .parentId(command.parentId())
                .sortOrder(command.sortOrder())
                .build();
        return persistRegionPort.save(region);
    }

    @Override
    @Transactional
    public AccommodationRegion update(Long id, UpdateRegionCommand command) {

        AccommodationRegion region = loadRegionPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND));
        region.update(command.regionName(), command.parentId(), command.sortOrder());
        return persistRegionPort.save(region);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        loadRegionPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND));
        persistRegionPort.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationRegion> getByAccommodationType(AccommodationType type) {

        return loadRegionPort.findByAccommodationType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationRegion> getAll() {

        return loadRegionPort.findAll();
    }
}
