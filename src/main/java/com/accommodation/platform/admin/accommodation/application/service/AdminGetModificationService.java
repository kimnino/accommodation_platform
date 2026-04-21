package com.accommodation.platform.admin.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.accommodation.application.port.in.AdminGetModificationQuery;
import com.accommodation.platform.core.accommodation.application.port.out.LoadModificationRequestPort;
import com.accommodation.platform.core.accommodation.domain.enums.ModificationStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminGetModificationService implements AdminGetModificationQuery {

    private final LoadModificationRequestPort loadModificationRequestPort;

    @Override
    public List<ModificationRequestSummary> listPending() {

        return loadModificationRequestPort.findByStatus(ModificationStatus.PENDING).stream()
                .map(data -> new ModificationRequestSummary(
                        data.id(),
                        data.accommodationId(),
                        data.partnerId(),
                        data.status().name(),
                        data.requestData(),
                        data.createdAt()))
                .toList();
    }
}
