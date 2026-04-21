package com.accommodation.platform.extranet.accommodation.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistModificationRequestPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetUpdateAccommodationUseCase;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetUpdateAccommodationService implements ExtranetUpdateAccommodationUseCase {

    private final LoadAccommodationPort loadAccommodationPort;
    private final PersistModificationRequestPort persistModificationRequestPort;
    private final ObjectMapper objectMapper;

    @Override
    public Long requestModification(Long accommodationId, Long partnerId, UpdateAccommodationCommand command) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        if (!accommodation.getPartnerId().equals(partnerId)) {
            throw new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다.");
        }

        String requestData = serializeCommand(command);
        return persistModificationRequestPort.save(accommodationId, partnerId, requestData);
    }

    private String serializeCommand(UpdateAccommodationCommand command) {

        try {
            return objectMapper.writeValueAsString(command);
        } catch (Exception e) {
            log.error("수정 요청 데이터 직렬화 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "수정 요청 처리 중 오류가 발생했습니다.");
        }
    }
}
