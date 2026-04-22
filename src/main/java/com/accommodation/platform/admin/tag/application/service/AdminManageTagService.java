package com.accommodation.platform.admin.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.tag.application.port.in.AdminGetTagQuery;
import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagUseCase;
import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.tag.application.port.out.LoadTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.LoadTagPort;
import com.accommodation.platform.core.tag.application.port.out.PersistTagPort;
import com.accommodation.platform.core.tag.domain.model.Tag;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminManageTagService implements AdminManageTagUseCase, AdminGetTagQuery {

    private final PersistTagPort persistTagPort;
    private final LoadTagPort loadTagPort;
    private final LoadTagGroupPort loadTagGroupPort;

    @Override
    public Tag create(Long tagGroupId, CreateTagCommand command) {

        loadTagGroupPort.findById(tagGroupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그 그룹을 찾을 수 없습니다."));

        Tag tag = Tag.builder()
                .tagGroupId(tagGroupId)
                .name(command.name())
                .displayOrder(command.displayOrder())
                .build();

        return persistTagPort.save(tag);
    }

    @Override
    public Tag update(Long tagId, UpdateTagCommand command) {

        Tag tag = findTagOrThrow(tagId);
        tag.updateInfo(command.name(), command.displayOrder());
        return persistTagPort.save(tag);
    }

    @Override
    public void deactivate(Long tagId) {

        Tag tag = findTagOrThrow(tagId);
        tag.deactivate();
        persistTagPort.save(tag);
    }

    @Override
    public void activate(Long tagId) {

        Tag tag = findTagOrThrow(tagId);
        tag.activate();
        persistTagPort.save(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> listByTagGroupId(Long tagGroupId) {

        return loadTagPort.findAllByTagGroupId(tagGroupId);
    }

    private Tag findTagOrThrow(Long tagId) {

        return loadTagPort.findTagById(tagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그를 찾을 수 없습니다."));
    }
}
