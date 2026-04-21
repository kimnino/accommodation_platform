package com.accommodation.platform.admin.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.application.port.out.LoadTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.PersistTagGroupPort;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminManageTagGroupService implements AdminManageTagGroupUseCase {

    private final PersistTagGroupPort persistTagGroupPort;
    private final LoadTagGroupPort loadTagGroupPort;

    @Override
    public TagGroup create(CreateTagGroupCommand command) {

        AccommodationType accommodationType = command.accommodationType() != null
                ? AccommodationType.valueOf(command.accommodationType())
                : null;

        TagGroup tagGroup = TagGroup.builder()
                .name(command.name())
                .displayOrder(command.displayOrder())
                .targetType(TagTarget.valueOf(command.targetType()))
                .accommodationType(accommodationType)
                .supplierId(command.supplierId())
                .build();

        return persistTagGroupPort.save(tagGroup);
    }

    @Override
    public TagGroup update(Long tagGroupId, UpdateTagGroupCommand command) {

        TagGroup tagGroup = loadTagGroupPort.findById(tagGroupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그 그룹을 찾을 수 없습니다."));

        AccommodationType accommodationType = command.accommodationType() != null
                ? AccommodationType.valueOf(command.accommodationType())
                : null;

        tagGroup.updateInfo(command.name(), command.displayOrder(), accommodationType);
        return persistTagGroupPort.save(tagGroup);
    }

    @Override
    public void deactivate(Long tagGroupId) {

        TagGroup tagGroup = loadTagGroupPort.findById(tagGroupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그 그룹을 찾을 수 없습니다."));

        tagGroup.deactivate();
        persistTagGroupPort.save(tagGroup);
    }

    @Override
    public void activate(Long tagGroupId) {

        TagGroup tagGroup = loadTagGroupPort.findById(tagGroupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그 그룹을 찾을 수 없습니다."));

        tagGroup.activate();
        persistTagGroupPort.save(tagGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagGroup> listAll() {

        return loadTagGroupPort.findAll();
    }
}
