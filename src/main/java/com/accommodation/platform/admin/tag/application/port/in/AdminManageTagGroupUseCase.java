package com.accommodation.platform.admin.tag.application.port.in;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

public interface AdminManageTagGroupUseCase {

    TagGroup create(CreateTagGroupCommand command);

    TagGroup update(Long tagGroupId, UpdateTagGroupCommand command);

    void deactivate(Long tagGroupId);

    void activate(Long tagGroupId);

    record CreateTagGroupCommand(
            String name,
            int displayOrder,
            TagTarget targetType,
            AccommodationType accommodationType,
            Long supplierId
    ) {
    }

    record UpdateTagGroupCommand(
            String name,
            Integer displayOrder,
            AccommodationType accommodationType
    ) {
    }
}
