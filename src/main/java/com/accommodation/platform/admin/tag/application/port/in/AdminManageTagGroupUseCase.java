package com.accommodation.platform.admin.tag.application.port.in;

import java.util.List;

import com.accommodation.platform.core.tag.domain.model.TagGroup;

public interface AdminManageTagGroupUseCase {

    TagGroup create(CreateTagGroupCommand command);

    TagGroup update(Long tagGroupId, UpdateTagGroupCommand command);

    void deactivate(Long tagGroupId);

    void activate(Long tagGroupId);

    List<TagGroup> listAll();

    record CreateTagGroupCommand(
            String name,
            int displayOrder,
            String targetType,
            String accommodationType,
            Long supplierId
    ) {
    }

    record UpdateTagGroupCommand(
            String name,
            Integer displayOrder,
            String accommodationType
    ) {
    }
}
