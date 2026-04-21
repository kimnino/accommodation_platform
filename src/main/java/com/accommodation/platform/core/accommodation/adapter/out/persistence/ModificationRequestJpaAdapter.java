package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadModificationRequestPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistModificationRequestPort;
import com.accommodation.platform.core.accommodation.domain.enums.ModificationStatus;

@Component
@RequiredArgsConstructor
public class ModificationRequestJpaAdapter implements PersistModificationRequestPort, LoadModificationRequestPort {

    private final AccommodationModificationRequestJpaRepository repository;

    @Override
    public Long save(Long accommodationId, Long partnerId, String requestData) {

        AccommodationModificationRequestJpaEntity entity = new AccommodationModificationRequestJpaEntity(
                accommodationId, partnerId, ModificationStatus.PENDING, requestData);
        AccommodationModificationRequestJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public void approve(Long modificationRequestId) {

        AccommodationModificationRequestJpaEntity entity = repository.findById(modificationRequestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODIFICATION_NOT_FOUND));
        entity.approve();
    }

    @Override
    public void reject(Long modificationRequestId, String reason) {

        AccommodationModificationRequestJpaEntity entity = repository.findById(modificationRequestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODIFICATION_NOT_FOUND));
        entity.reject(reason);
    }

    @Override
    public Optional<ModificationRequestData> findById(Long id) {

        return repository.findById(id).map(this::toData);
    }

    @Override
    public List<ModificationRequestData> findByStatus(ModificationStatus status) {

        return repository.findByStatus(status).stream()
                .map(this::toData)
                .toList();
    }

    private ModificationRequestData toData(AccommodationModificationRequestJpaEntity entity) {

        return new ModificationRequestData(
                entity.getId(),
                entity.getAccommodationId(),
                entity.getPartnerId(),
                entity.getStatus(),
                entity.getRequestData(),
                entity.getCreatedAt());
    }
}
