package com.accommodation.platform.core.tag.application.port.out;

import java.util.List;

public interface LoadAccommodationTagPort {

    List<Long> findTagIdsByAccommodationId(Long accommodationId);
}
