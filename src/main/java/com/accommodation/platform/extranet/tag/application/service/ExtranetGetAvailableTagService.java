package com.accommodation.platform.extranet.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.tag.application.port.out.LoadTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.LoadTagPort;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;
import com.accommodation.platform.extranet.tag.application.port.in.ExtranetGetAvailableTagQuery;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtranetGetAvailableTagService implements ExtranetGetAvailableTagQuery {

    private final LoadTagGroupPort loadTagGroupPort;
    private final LoadTagPort loadTagPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<TagGroup> getAvailableTagGroups(Long accommodationId, Long partnerId, String targetType) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        if (!accommodation.getPartnerId().equals(partnerId)) {
            throw new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다.");
        }

        return loadTagGroupPort.findByTargetTypeAndAccommodationType(
                TagTarget.valueOf(targetType), accommodation.getType());
    }

    @Override
    public List<Tag> getTagsByGroupId(Long tagGroupId) {

        return loadTagPort.findByTagGroupId(tagGroupId);
    }
}
