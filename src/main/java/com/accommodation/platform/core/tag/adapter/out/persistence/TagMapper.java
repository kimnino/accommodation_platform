package com.accommodation.platform.core.tag.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

@Component
public class TagMapper {

    public TagGroup toTagGroupDomain(TagGroupJpaEntity entity) {

        TagGroup tagGroup = TagGroup.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayOrder(entity.getDisplayOrder())
                .targetType(entity.getTargetType())
                .accommodationType(entity.getAccommodationType())
                .build();

        tagGroup.setCreatedAt(entity.getCreatedAt());
        tagGroup.setUpdatedAt(entity.getUpdatedAt());

        if (!entity.isActive()) {
            tagGroup.deactivate();
        }

        return tagGroup;
    }

    public TagGroupJpaEntity toJpaEntity(TagGroup domain) {

        return new TagGroupJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getDisplayOrder(),
                domain.getTargetType(),
                domain.getAccommodationType(),
                domain.isActive());
    }

    public Tag toTagDomain(TagJpaEntity entity) {

        Tag tag = Tag.builder()
                .id(entity.getId())
                .tagGroupId(entity.getTagGroupId())
                .name(entity.getName())
                .displayOrder(entity.getDisplayOrder())
                .build();

        tag.setCreatedAt(entity.getCreatedAt());
        tag.setUpdatedAt(entity.getUpdatedAt());

        if (!entity.isActive()) {
            tag.deactivate();
        }

        return tag;
    }

    public TagJpaEntity toJpaEntity(Tag domain) {

        return new TagJpaEntity(
                domain.getId(),
                domain.getTagGroupId(),
                domain.getName(),
                domain.getDisplayOrder(),
                domain.isActive());
    }
}
