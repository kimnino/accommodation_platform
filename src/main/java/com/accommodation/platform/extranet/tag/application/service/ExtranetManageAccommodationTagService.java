package com.accommodation.platform.extranet.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.tag.application.port.out.LoadAccommodationTagPort;
import com.accommodation.platform.core.tag.application.port.out.PersistAccommodationTagPort;
import com.accommodation.platform.extranet.common.ExtranetOwnershipVerifier;
import com.accommodation.platform.extranet.tag.application.port.in.ExtranetManageAccommodationTagUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetManageAccommodationTagService implements ExtranetManageAccommodationTagUseCase {

    private final LoadAccommodationTagPort loadAccommodationTagPort;
    private final PersistAccommodationTagPort persistAccommodationTagPort;
    private final ExtranetOwnershipVerifier ownershipVerifier;

    @Override
    public void addTags(Long accommodationId, Long partnerId, List<Long> tagIds) {

        ownershipVerifier.verifyAccommodationOwnership(accommodationId, partnerId);

        List<Long> existingTagIds = loadAccommodationTagPort.findTagIdsByAccommodationId(accommodationId);
        tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .forEach(tagId -> persistAccommodationTagPort.addTag(accommodationId, tagId));
    }

    @Override
    public void removeTags(Long accommodationId, Long partnerId, List<Long> tagIds) {

        ownershipVerifier.verifyAccommodationOwnership(accommodationId, partnerId);
        persistAccommodationTagPort.removeTags(accommodationId, tagIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getTagIds(Long accommodationId, Long partnerId) {

        ownershipVerifier.verifyAccommodationOwnership(accommodationId, partnerId);
        return loadAccommodationTagPort.findTagIdsByAccommodationId(accommodationId);
    }
}
