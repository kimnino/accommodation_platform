package com.accommodation.platform.extranet.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.tag.adapter.out.persistence.AccommodationTagJpaEntity;
import com.accommodation.platform.core.tag.adapter.out.persistence.AccommodationTagJpaRepository;
import com.accommodation.platform.extranet.tag.application.port.in.ExtranetManageAccommodationTagUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetManageAccommodationTagService implements ExtranetManageAccommodationTagUseCase {

    private final AccommodationTagJpaRepository accommodationTagJpaRepository;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public void addTags(Long accommodationId, Long partnerId, List<Long> tagIds) {

        verifyOwnership(accommodationId, partnerId);

        List<Long> existingTagIds = accommodationTagJpaRepository.findByAccommodationId(accommodationId)
                .stream()
                .map(AccommodationTagJpaEntity::getTagId)
                .toList();

        tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .map(tagId -> new AccommodationTagJpaEntity(accommodationId, tagId))
                .forEach(accommodationTagJpaRepository::save);
    }

    @Override
    public void removeTags(Long accommodationId, Long partnerId, List<Long> tagIds) {

        verifyOwnership(accommodationId, partnerId);
        accommodationTagJpaRepository.deleteByAccommodationIdAndTagIdIn(accommodationId, tagIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getTagIds(Long accommodationId, Long partnerId) {

        verifyOwnership(accommodationId, partnerId);
        return accommodationTagJpaRepository.findByAccommodationId(accommodationId)
                .stream()
                .map(AccommodationTagJpaEntity::getTagId)
                .toList();
    }

    private void verifyOwnership(Long accommodationId, Long partnerId) {

        loadAccommodationPort.findById(accommodationId)
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));
    }
}
