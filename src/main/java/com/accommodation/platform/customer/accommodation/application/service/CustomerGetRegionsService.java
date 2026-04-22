package com.accommodation.platform.customer.accommodation.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationRegionPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetRegionsQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerGetRegionsService implements CustomerGetRegionsQuery {

    private final LoadAccommodationRegionPort loadAccommodationRegionPort;

    @Override
    public List<RegionNode> getRegions(AccommodationType accommodationType) {

        List<AccommodationRegion> all = accommodationType != null
                ? loadAccommodationRegionPort.findByAccommodationType(accommodationType)
                : loadAccommodationRegionPort.findAll();

        return buildTree(all, null);
    }

    private List<RegionNode> buildTree(List<AccommodationRegion> all, Long parentId) {

        return all.stream()
                .filter(r -> parentId == null ? r.getParentId() == null : parentId.equals(r.getParentId()))
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(r -> new RegionNode(r.getId(), r.getRegionName(), r.getParentId(),
                        r.getSortOrder(), buildTree(all, r.getId())))
                .toList();
    }
}
