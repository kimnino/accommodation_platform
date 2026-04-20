package com.accommodation.platform.admin.accommodation.application.port.in;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

public interface AdminApproveAccommodationUseCase {

    Accommodation approve(Long accommodationId);

    Accommodation suspend(Long accommodationId);

    Accommodation close(Long accommodationId);
}
