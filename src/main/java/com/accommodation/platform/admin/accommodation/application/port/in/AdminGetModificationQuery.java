package com.accommodation.platform.admin.accommodation.application.port.in;

import java.time.Instant;
import java.util.List;

public interface AdminGetModificationQuery {

    List<ModificationRequestSummary> listPending();

    record ModificationRequestSummary(
            Long id,
            Long accommodationId,
            Long partnerId,
            String status,
            String requestData,
            Instant createdAt
    ) {
    }
}
