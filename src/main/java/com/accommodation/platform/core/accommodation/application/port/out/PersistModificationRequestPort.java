package com.accommodation.platform.core.accommodation.application.port.out;

public interface PersistModificationRequestPort {

    Long save(Long accommodationId, Long partnerId, String requestData);

    void approve(Long modificationRequestId);

    void reject(Long modificationRequestId, String reason);
}
