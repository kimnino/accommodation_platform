package com.accommodation.platform.core.tag.application.port.out;

import java.util.List;

public interface PersistAccommodationTagPort {

    void addTag(Long accommodationId, Long tagId);

    void removeTags(Long accommodationId, List<Long> tagIds);
}
