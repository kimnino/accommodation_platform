package com.accommodation.platform.admin.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface AdminListAccommodationQuery {

    List<Accommodation> listAll();

    Accommodation getById(Long accommodationId);
}
