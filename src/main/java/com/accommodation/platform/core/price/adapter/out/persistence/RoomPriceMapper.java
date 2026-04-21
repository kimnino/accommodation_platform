package com.accommodation.platform.core.price.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RoomPriceMapper {

    RoomPrice toDomain(RoomPriceJpaEntity entity);

    RoomPriceJpaEntity toJpaEntity(RoomPrice domain);

    @AfterMapping
    default void restoreJpaTimestamps(@MappingTarget RoomPriceJpaEntity entity, RoomPrice domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }
}
