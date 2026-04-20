package com.accommodation.platform.extranet.tag.application.port.in;

import java.util.List;

public interface ExtranetManageAccommodationTagUseCase {

    void addTags(Long accommodationId, Long partnerId, List<Long> tagIds);

    void removeTags(Long accommodationId, Long partnerId, List<Long> tagIds);

    List<Long> getTagIds(Long accommodationId, Long partnerId);
}
