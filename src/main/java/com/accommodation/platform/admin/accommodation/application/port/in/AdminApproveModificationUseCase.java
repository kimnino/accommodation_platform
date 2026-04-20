package com.accommodation.platform.admin.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationModificationRequestJpaEntity;

public interface AdminApproveModificationUseCase {

    List<AccommodationModificationRequestJpaEntity> listPending();

    void approve(Long modificationRequestId);

    void reject(Long modificationRequestId, String reason);
}
