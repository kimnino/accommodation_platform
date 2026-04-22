package com.accommodation.platform.core.accommodation.domain.model;

public record AccommodationTranslation(
        Long accommodationId,
        String locale,
        String name,
        String fullAddress,
        String locationDescription,
        String introduction,
        String serviceAndFacilities,
        String usageInfo,
        String cancellationAndRefundPolicy
) {}
