package com.accommodation.platform.admin.tag.application.port.in;

import java.util.List;

import com.accommodation.platform.core.tag.domain.model.Tag;

public interface AdminGetTagQuery {

    List<Tag> listByTagGroupId(Long tagGroupId);
}
