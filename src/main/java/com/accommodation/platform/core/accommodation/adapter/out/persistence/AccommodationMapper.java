package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.accommodation.domain.model.AccommodationImage;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface AccommodationMapper {

    Accommodation toDomain(AccommodationJpaEntity entity);

    @Mapping(target = "images", ignore = true)
    AccommodationJpaEntity toJpaEntity(Accommodation domain);

    @AfterMapping
    default void restoreAccommodationState(@MappingTarget Accommodation accommodation, AccommodationJpaEntity entity) {
        accommodation.restoreStatus(entity.getStatus());
        entity.getImages().forEach(img -> accommodation.addImage(
                new AccommodationImage(img.getRelativePath(), img.getCategory(), img.getDisplayOrder(), img.isPrimary())));
    }

    @AfterMapping
    default void populateJpaImages(@MappingTarget AccommodationJpaEntity entity, Accommodation domain) {
        domain.getImages().stream()
                .map(img -> new AccommodationImageJpaEntity(
                        domain.getId(), img.relativePath(), img.category(), img.displayOrder(), img.isPrimary()))
                .forEach(entity.getImages()::add);
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }
}
