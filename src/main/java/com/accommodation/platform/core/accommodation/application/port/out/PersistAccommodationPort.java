package com.accommodation.platform.core.accommodation.application.port.out;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface PersistAccommodationPort {

    Accommodation save(Accommodation accommodation);

    void delete(Long id);
}
