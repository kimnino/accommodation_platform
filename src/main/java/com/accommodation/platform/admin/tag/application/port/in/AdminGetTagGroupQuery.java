package com.accommodation.platform.admin.tag.application.port.in;

import java.util.List;

import com.accommodation.platform.core.tag.domain.model.TagGroup;

public interface AdminGetTagGroupQuery {

    List<TagGroup> listAll();
}
