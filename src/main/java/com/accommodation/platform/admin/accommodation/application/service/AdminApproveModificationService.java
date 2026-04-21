package com.accommodation.platform.admin.accommodation.application.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

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
    @Transactional(readOnly = true)
    public List<AccommodationModificationRequestJpaEntity> listPending() {

        return modificationRequestRepository.findByStatus(ModificationStatus.PENDING);
    }

    @Override
    public void approve(Long modificationRequestId) {

        AccommodationModificationRequestJpaEntity request = findRequest(modificationRequestId);
        validatePending(request);

        UpdateAccommodationCommand command = deserializeCommand(request.getRequestData());

        Accommodation accommodation = loadAccommodationPort.findById(request.getAccommodationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        // null인 필드는 기존 값을 유지하여 덮어쓰기를 방지한다.
        String name = command.name() != null ? command.name() : accommodation.getName();
        String fullAddress = command.fullAddress() != null ? command.fullAddress() : accommodation.getFullAddress();
        Double latitude = command.latitude() != null ? command.latitude() : accommodation.getLatitude();
        Double longitude = command.longitude() != null ? command.longitude() : accommodation.getLongitude();
        String locationDescription = command.locationDescription() != null
                ? command.locationDescription()
                : accommodation.getLocationDescription();
        LocalTime checkInTime = command.checkInTime() != null
                ? LocalTime.parse(command.checkInTime())
                : accommodation.getCheckInTime();
        LocalTime checkOutTime = command.checkOutTime() != null
                ? LocalTime.parse(command.checkOutTime())
                : accommodation.getCheckOutTime();

        accommodation.updateInfo(
                name,
                fullAddress,
                latitude,
                longitude,
                locationDescription,
                checkInTime,
                checkOutTime);

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
