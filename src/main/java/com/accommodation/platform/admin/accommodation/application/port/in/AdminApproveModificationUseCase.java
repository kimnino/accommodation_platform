package com.accommodation.platform.admin.accommodation.application.port.in;

public interface AdminApproveModificationUseCase {

    void approve(Long modificationRequestId);

    void reject(Long modificationRequestId, String reason);
}
