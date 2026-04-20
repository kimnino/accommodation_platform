package com.accommodation.platform.core.tag.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.tag.domain.model.Tag;

public interface LoadTagPort {

    Optional<Tag> findTagById(Long id);

    List<Tag> findByTagGroupId(Long tagGroupId);
}
