package com.accommodation.platform.extranet.tag.application.port.in;

import java.util.List;

import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

public interface ExtranetGetAvailableTagQuery {

    List<TagGroup> getAvailableTagGroups(Long accommodationId, Long partnerId, String targetType);

    List<Tag> getTagsByGroupId(Long tagGroupId);
}
