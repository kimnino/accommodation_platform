package com.accommodation.platform.extranet.inventory.application.service;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationHourlySettingJpaEntity;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationHourlySettingJpaRepository;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.extranet.inventory.application.port.in.ExtranetSetHourlySettingUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetSetHourlySettingService implements ExtranetSetHourlySettingUseCase {

    private final AccommodationHourlySettingJpaRepository settingRepository;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public void setSetting(Long accommodationId, Long partnerId, SetHourlySettingCommand command) {

        loadAccommodationPort.findById(accommodationId)
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));

        settingRepository.findByAccommodationId(accommodationId)
                .ifPresentOrElse(
                        settingRepository::delete,
                        () -> {});

        int slotUnit = command.slotUnitMinutes();
        if (slotUnit != 30 && slotUnit != 60) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "슬롯 단위는 30분 또는 60분만 가능합니다.");
        }

        AccommodationHourlySettingJpaEntity setting = new AccommodationHourlySettingJpaEntity(
                accommodationId,
                LocalTime.parse(command.operatingStartTime()),
                LocalTime.parse(command.operatingEndTime()),
                command.usageDurationMinutes(),
                command.bufferMinutes(),
                slotUnit);

        settingRepository.save(setting);
    }
}
