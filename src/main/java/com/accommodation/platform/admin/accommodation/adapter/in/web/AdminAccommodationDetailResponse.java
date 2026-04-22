package com.accommodation.platform.admin.accommodation.adapter.in.web;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationImage;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationTranslation;

public record AdminAccommodationDetailResponse(
        Long id,
        String name,
        String type,
        String status,
        String fullAddress,
        double latitude,
        double longitude,
        String locationDescription,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        List<ImageResponse> images,
        List<TranslationResponse> translations,
        Instant createdAt,
        Instant updatedAt
) {

    public static AdminAccommodationDetailResponse from(
            Accommodation accommodation,
            List<AccommodationTranslation> translations) {

        List<ImageResponse> imageResponses = accommodation.getImages().stream()
                .map(ImageResponse::from)
                .toList();

        List<TranslationResponse> translationResponses = translations.stream()
                .map(TranslationResponse::from)
                .toList();

        return new AdminAccommodationDetailResponse(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getType().name(),
                accommodation.getStatus().name(),
                accommodation.getFullAddress(),
                accommodation.getLatitude(),
                accommodation.getLongitude(),
                accommodation.getLocationDescription(),
                accommodation.getCheckInTime(),
                accommodation.getCheckOutTime(),
                imageResponses,
                translationResponses,
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

    public record TranslationResponse(
            String locale,
            String name,
            String fullAddress,
            String locationDescription,
            String introduction,
            String serviceAndFacilities,
            String usageInfo,
            String cancellationAndRefundPolicy
    ) {

        public static TranslationResponse from(AccommodationTranslation translation) {

            return new TranslationResponse(
                    translation.locale(),
                    translation.name(),
                    translation.fullAddress(),
                    translation.locationDescription(),
                    translation.introduction(),
                    translation.serviceAndFacilities(),
                    translation.usageInfo(),
                    translation.cancellationAndRefundPolicy());
        }
    }
}
