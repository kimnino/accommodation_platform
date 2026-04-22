package com.accommodation.platform.customer.tag.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;

public interface CustomerGetTagsQuery {

    List<TagGroupResponse> getTagGroups(TagTarget targetType, AccommodationType accommodationType);

    record TagGroupResponse(
            Long groupId,
            String groupName,
            String targetType,
            String accommodationType,
            List<TagResponse> tags
    ) {}

    record TagResponse(
            Long tagId,
            String name,
            int displayOrder
    ) {}
}
