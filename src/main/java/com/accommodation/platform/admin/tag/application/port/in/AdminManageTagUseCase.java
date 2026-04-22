package com.accommodation.platform.admin.tag.application.port.in;

import com.accommodation.platform.core.tag.domain.model.Tag;

public interface AdminManageTagUseCase {

    Tag create(Long tagGroupId, CreateTagCommand command);

    Tag update(Long tagId, UpdateTagCommand command);

    void deactivate(Long tagId);

    void activate(Long tagId);

    record CreateTagCommand(
            String name,
            int displayOrder
    ) {
    }

    record UpdateTagCommand(
            String name,
            Integer displayOrder
    ) {
    }
}
