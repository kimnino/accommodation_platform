package com.accommodation.platform.extranet.accommodation.adapter.in.web;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationImage;

public record AccommodationDetailResponse(
        Long id,
        String name,
        String type,
        String status,
        Long regionId,
        String fullAddress,
        double latitude,
        double longitude,
        String locationDescription,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        List<ImageResponse> images,
        Instant createdAt,
        Instant updatedAt
) {

    public static AccommodationDetailResponse from(Accommodation accommodation) {

        List<ImageResponse> imageResponses = accommodation.getImages().stream()
                .map(ImageResponse::from)
                .toList();

        return new AccommodationDetailResponse(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getType().name(),
                accommodation.getStatus().name(),
                accommodation.getRegionId(),
                accommodation.getFullAddress(),
                accommodation.getLatitude(),
                accommodation.getLongitude(),
                accommodation.getLocationDescription(),
                accommodation.getCheckInTime(),
                accommodation.getCheckOutTime(),
                imageResponses,
                accommodation.getCreatedAt(),
                accommodation.getUpdatedAt());
    }

    public record ImageResponse(String relativePath, String category, int displayOrder, boolean isPrimary) {

        public static ImageResponse from(AccommodationImage image) {

            return new ImageResponse(
                    image.relativePath(),
                    image.category().name(),
                    image.displayOrder(),
                    image.isPrimary());
        }
    }
}
