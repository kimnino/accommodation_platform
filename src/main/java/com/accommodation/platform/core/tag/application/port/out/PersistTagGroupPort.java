package com.accommodation.platform.core.tag.application.port.out;

import com.accommodation.platform.core.tag.domain.model.TagGroup;

public interface PersistTagGroupPort {

    TagGroup save(TagGroup tagGroup);

    void delete(Long id);
}
