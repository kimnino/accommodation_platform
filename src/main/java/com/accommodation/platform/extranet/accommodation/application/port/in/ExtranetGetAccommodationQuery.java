package com.accommodation.platform.extranet.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface ExtranetGetAccommodationQuery {

    Accommodation getById(Long accommodationId, Long partnerId);

    List<Accommodation> getByPartnerId(Long partnerId);
}
