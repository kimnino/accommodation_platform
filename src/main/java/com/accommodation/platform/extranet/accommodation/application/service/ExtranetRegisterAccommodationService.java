package com.accommodation.platform.extranet.accommodation.application.service;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationSupportedLocaleJpaEntity;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.enums.SupportedLocale;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetRegisterAccommodationService implements ExtranetRegisterAccommodationUseCase {

    private final PersistAccommodationPort persistAccommodationPort;
    private final PersistAccommodationTranslationPort persistTranslationPort;
    private final EntityManager entityManager;

    @Override
    public Accommodation register(RegisterAccommodationCommand command) {

        Accommodation accommodation = Accommodation.builder()
                .partnerId(command.partnerId())
                .name(command.name())
                .type(AccommodationType.valueOf(command.type()))
                .fullAddress(command.fullAddress())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .locationDescription(command.locationDescription())
                .checkInTime(command.checkInTime() != null ? LocalTime.parse(command.checkInTime()) : null)
                .checkOutTime(command.checkOutTime() != null ? LocalTime.parse(command.checkOutTime()) : null)
                .build();

        Accommodation saved = persistAccommodationPort.save(accommodation);

        this.saveSupportedLocales(saved.getId(), command.supportedLocales());
        this.saveTranslations(saved.getId(), command.translations());

        return saved;
    }

    private void saveSupportedLocales(Long accommodationId, List<String> locales) {

        if (locales == null || locales.isEmpty()) {
            return;
        }

        for (String localeCode : locales) {
            String code = localeCode.toLowerCase();
            if (!SupportedLocale.isValid(code)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "지원하지 않는 언어코드입니다: " + code);
            }
            entityManager.persist(new AccommodationSupportedLocaleJpaEntity(accommodationId, code));
        }
    }

    private void saveTranslations(Long accommodationId, List<TranslationCommand> translations) {

        if (translations == null || translations.isEmpty()) {
            return;
        }

        List<AccommodationTranslationJpaEntity> entities = translations.stream()
                .map(t -> new AccommodationTranslationJpaEntity(
                        accommodationId,
                        t.locale(),
                        t.name(),
                        t.fullAddress(),
                        t.locationDescription(),
                        t.introduction(),
                        t.serviceAndFacilities(),
                        t.usageInfo(),
                        t.cancellationAndRefundPolicy()))
                .toList();

        persistTranslationPort.saveAll(entities);
    }
}
