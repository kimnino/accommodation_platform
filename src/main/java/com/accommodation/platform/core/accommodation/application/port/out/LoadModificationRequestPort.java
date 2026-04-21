package com.accommodation.platform.core.accommodation.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.accommodation.domain.enums.ModificationStatus;

public interface LoadModificationRequestPort {

    Optional<ModificationRequestData> findById(Long id);

    List<ModificationRequestData> findByStatus(ModificationStatus status);

    record ModificationRequestData(
            Long id,
            Long accommodationId,
            Long partnerId,
            ModificationStatus status,
            String requestData,
            Instant createdAt
    ) {
    }
}
