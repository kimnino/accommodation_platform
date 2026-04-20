package com.accommodation.platform.admin.accommodation.application.service;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminApproveModificationUseCase;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationModificationRequestJpaEntity;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationModificationRequestJpaRepository;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.enums.ModificationStatus;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetUpdateAccommodationUseCase.UpdateAccommodationCommand;

import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminApproveModificationService implements AdminApproveModificationUseCase {

    private final AccommodationModificationRequestJpaRepository modificationRequestRepository;
    private final LoadAccommodationPort loadAccommodationPort;
    private final PersistAccommodationPort persistAccommodationPort;
    private final ObjectMapper objectMapper;

    @Override
    public void approve(Long modificationRequestId) {

        AccommodationModificationRequestJpaEntity request = findRequest(modificationRequestId);
        validatePending(request);

        UpdateAccommodationCommand command = deserializeCommand(request.getRequestData());

        Accommodation accommodation = loadAccommodationPort.findById(request.getAccommodationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        accommodation.updateInfo(
                command.name(),
                command.fullAddress(),
                command.latitude(),
                command.longitude(),
                command.locationDescription(),
                command.checkInTime() != null ? LocalTime.parse(command.checkInTime()) : null,
                command.checkOutTime() != null ? LocalTime.parse(command.checkOutTime()) : null);

        persistAccommodationPort.save(accommodation);
        request.approve();
    }

    @Override
    public void reject(Long modificationRequestId, String reason) {

        AccommodationModificationRequestJpaEntity request = findRequest(modificationRequestId);
        validatePending(request);
        request.reject(reason);
    }

    private AccommodationModificationRequestJpaEntity findRequest(Long id) {

        return modificationRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODIFICATION_NOT_FOUND));
    }

    private void validatePending(AccommodationModificationRequestJpaEntity request) {

        if (request.getStatus() != ModificationStatus.PENDING) {
            throw new BusinessException(ErrorCode.MODIFICATION_ALREADY_PROCESSED);
        }
    }

    private UpdateAccommodationCommand deserializeCommand(String json) {

        try {
            return objectMapper.readValue(json, UpdateAccommodationCommand.class);
        } catch (Exception e) {
            log.error("수정 요청 데이터 역직렬화 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "수정 요청 데이터 처리 중 오류가 발생했습니다.");
        }
    }
}
