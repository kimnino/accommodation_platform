package com.accommodation.platform.core.tag.application.port.out;

import com.accommodation.platform.core.tag.domain.model.Tag;

public interface PersistTagPort {

    Tag save(Tag tag);

    void delete(Long id);
}
