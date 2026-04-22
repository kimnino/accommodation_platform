package com.accommodation.platform.customer.accommodation.application.port.in;

import java.util.List;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

public interface CustomerGetRegionsQuery {

    List<RegionNode> getRegions(AccommodationType accommodationType);

    record RegionNode(
            Long id,
            String regionName,
            Long parentId,
            int sortOrder,
            List<RegionNode> children
    ) {}
}
