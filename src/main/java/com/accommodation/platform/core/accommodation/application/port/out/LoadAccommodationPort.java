package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface LoadAccommodationPort {

    Optional<Accommodation> findById(Long id);

    List<Accommodation> findByPartnerId(Long partnerId);

    List<Accommodation> findAll();

    List<Accommodation> findByStatus(AccommodationStatus status);
}
