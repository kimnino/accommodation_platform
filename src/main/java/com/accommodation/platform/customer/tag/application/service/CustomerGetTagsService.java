package com.accommodation.platform.customer.tag.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.application.port.out.LoadTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.LoadTagPort;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;
import com.accommodation.platform.customer.tag.application.port.in.CustomerGetTagsQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerGetTagsService implements CustomerGetTagsQuery {

    private final LoadTagGroupPort loadTagGroupPort;
    private final LoadTagPort loadTagPort;

    @Override
    public List<TagGroupResponse> getTagGroups(TagTarget targetType, AccommodationType accommodationType) {

        List<TagGroup> groups = (targetType != null || accommodationType != null)
                ? loadTagGroupPort.findByTargetTypeAndAccommodationType(
                        targetType != null ? targetType : TagTarget.ACCOMMODATION, accommodationType)
                : loadTagGroupPort.findAll();

        return groups.stream()
                .filter(TagGroup::isActive)
                .map(group -> {
                    List<TagResponse> tags = loadTagPort.findByTagGroupId(group.getId()).stream()
                            .filter(Tag::isActive)
                            .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                            .map(t -> new TagResponse(t.getId(), t.getName(), t.getDisplayOrder()))
                            .toList();
                    return new TagGroupResponse(
                            group.getId(),
                            group.getName(),
                            group.getTargetType().name(),
                            group.getAccommodationType() != null ? group.getAccommodationType().name() : null,
                            tags);
                })
                .toList();
    }
}
