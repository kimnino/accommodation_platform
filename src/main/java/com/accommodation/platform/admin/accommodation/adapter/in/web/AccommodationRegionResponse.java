package com.accommodation.platform.admin.accommodation.adapter.in.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.accommodation.platform.core.accommodation.domain.model.AccommodationRegion;

public record AccommodationRegionResponse(
        Long id,
        String accommodationType,
        String regionName,
        Long parentId,
        int sortOrder,
        List<AccommodationRegionResponse> children
) {

    public static AccommodationRegionResponse from(AccommodationRegion region) {
        return new AccommodationRegionResponse(
                region.getId(),
                region.getAccommodationType().name(),
                region.getRegionName(),
                region.getParentId(),
                region.getSortOrder(),
                new ArrayList<>()
        );
    }

    public static List<AccommodationRegionResponse> toTree(List<AccommodationRegion> regions) {
        Map<Long, AccommodationRegionResponse> map = regions.stream()
                .collect(Collectors.toMap(
                        AccommodationRegion::getId,
                        AccommodationRegionResponse::from));

        List<AccommodationRegionResponse> roots = new ArrayList<>();
        for (AccommodationRegion region : regions) {
            AccommodationRegionResponse node = map.get(region.getId());
            if (region.getParentId() == null) {
                roots.add(node);
            } else {
                AccommodationRegionResponse parent = map.get(region.getParentId());
                if (parent != null) {
                    parent.children().add(node);
                }
            }
        }
        return roots;
    }
}
