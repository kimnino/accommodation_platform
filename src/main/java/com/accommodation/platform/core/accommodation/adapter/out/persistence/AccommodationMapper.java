package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationImage;

@Component
public class AccommodationMapper {

    public Accommodation toDomain(AccommodationJpaEntity entity) {

        Accommodation accommodation = Accommodation.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .name(entity.getName())
                .type(entity.getType())
                .fullAddress(entity.getFullAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .locationDescription(entity.getLocationDescription())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .build();

        accommodation.restoreStatus(entity.getStatus());
        accommodation.setCreatedAt(entity.getCreatedAt());
        accommodation.setUpdatedAt(entity.getUpdatedAt());

        for (AccommodationImageJpaEntity imageEntity : entity.getImages()) {
            accommodation.addImage(new AccommodationImage(
                    imageEntity.getRelativePath(),
                    imageEntity.getCategory(),
                    imageEntity.getDisplayOrder(),
                    imageEntity.isPrimary()));
        }

        return accommodation;
    }

    public AccommodationJpaEntity toJpaEntity(Accommodation domain) {

        AccommodationJpaEntity entity = new AccommodationJpaEntity(
                domain.getId(),
                domain.getPartnerId(),
                domain.getName(),
                domain.getType(),
                domain.getFullAddress(),
                domain.getLatitude(),
                domain.getLongitude(),
                domain.getLocationDescription(),
                domain.getStatus(),
                domain.getCheckInTime(),
                domain.getCheckOutTime());

        domain.getImages().stream()
                .map(image -> new AccommodationImageJpaEntity(
                        domain.getId(),
                        image.relativePath(),
                        image.category(),
                        image.displayOrder(),
                        image.isPrimary()))
                .forEach(entity.getImages()::add);

        return entity;
    }
}
