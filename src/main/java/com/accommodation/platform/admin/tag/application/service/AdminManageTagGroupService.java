package com.accommodation.platform.admin.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.tag.application.port.in.AdminGetTagGroupQuery;
import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.tag.application.port.out.LoadTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.PersistTagGroupPort;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminManageTagGroupService implements AdminManageTagGroupUseCase, AdminGetTagGroupQuery {

    private final PersistTagGroupPort persistTagGroupPort;
    private final LoadTagGroupPort loadTagGroupPort;

    @Override
    public TagGroup create(CreateTagGroupCommand command) {

        TagGroup tagGroup = TagGroup.builder()
                .name(command.name())
                .displayOrder(command.displayOrder())
                .targetType(command.targetType())
                .accommodationType(command.accommodationType())
                .supplierId(command.supplierId())
                .build();

        return persistTagGroupPort.save(tagGroup);
    }

    @Override
    public TagGroup update(Long tagGroupId, UpdateTagGroupCommand command) {

        TagGroup tagGroup = findTagGroupOrThrow(tagGroupId);
        tagGroup.updateInfo(command.name(), command.displayOrder(), command.accommodationType());
        return persistTagGroupPort.save(tagGroup);
    }

    @Override
    public void deactivate(Long tagGroupId) {

        TagGroup tagGroup = findTagGroupOrThrow(tagGroupId);
        tagGroup.deactivate();
        persistTagGroupPort.save(tagGroup);
    }

    @Override
    public void activate(Long tagGroupId) {

        TagGroup tagGroup = findTagGroupOrThrow(tagGroupId);
        tagGroup.activate();
        persistTagGroupPort.save(tagGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagGroup> listAll() {

        return loadTagGroupPort.findAll();
    }

    private TagGroup findTagGroupOrThrow(Long tagGroupId) {

        return loadTagGroupPort.findById(tagGroupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그 그룹을 찾을 수 없습니다."));
    }
}
