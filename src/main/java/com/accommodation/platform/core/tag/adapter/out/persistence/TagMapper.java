package com.accommodation.platform.core.tag.adapter.out.persistence;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface TagMapper {

    @Mapping(source = "active", target = "isActive")
    TagGroup toTagGroupDomain(TagGroupJpaEntity entity);

    @Mapping(source = "active", target = "isActive")
    TagGroupJpaEntity toJpaEntity(TagGroup domain);

    @Mapping(source = "active", target = "isActive")
    Tag toTagDomain(TagJpaEntity entity);

    @Mapping(source = "active", target = "isActive")
    TagJpaEntity toJpaEntity(Tag domain);

    @AfterMapping
    default void restoreTagGroupJpaTimestamps(@MappingTarget TagGroupJpaEntity entity, TagGroup domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }

    @AfterMapping
    default void restoreTagJpaTimestamps(@MappingTarget TagJpaEntity entity, Tag domain) {
        if (domain.getCreatedAt() != null) {
            entity.restoreTimestamps(domain.getCreatedAt(), domain.getUpdatedAt());
        }
    }
}
