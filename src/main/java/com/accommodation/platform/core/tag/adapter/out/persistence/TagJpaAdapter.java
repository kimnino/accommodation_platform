package com.accommodation.platform.core.tag.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.application.port.out.LoadTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.LoadTagPort;
import com.accommodation.platform.core.tag.application.port.out.PersistTagGroupPort;
import com.accommodation.platform.core.tag.application.port.out.PersistTagPort;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

@Repository
@RequiredArgsConstructor
public class TagJpaAdapter implements PersistTagGroupPort, LoadTagGroupPort, PersistTagPort, LoadTagPort {

    private final TagGroupJpaRepository tagGroupJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final TagMapper mapper;

    @Override
    public TagGroup save(TagGroup tagGroup) {

        TagGroupJpaEntity entity = mapper.toJpaEntity(tagGroup);
        TagGroupJpaEntity saved = tagGroupJpaRepository.save(entity);
        return mapper.toTagGroupDomain(saved);
    }

    @Override
    public void delete(Long id) {

        tagGroupJpaRepository.deleteById(id);
    }

    @Override
    public Optional<TagGroup> findById(Long id) {

        return tagGroupJpaRepository.findById(id)
                .map(mapper::toTagGroupDomain);
    }

    @Override
    public List<TagGroup> findAll() {

        return tagGroupJpaRepository.findAll().stream()
                .map(mapper::toTagGroupDomain)
                .toList();
    }

    @Override
    public List<TagGroup> findByTargetTypeAndAccommodationType(TagTarget targetType,
                                                               AccommodationType accommodationType) {

        return tagGroupJpaRepository.findByTargetTypeAndAccommodationType(targetType, accommodationType)
                .stream()
                .map(mapper::toTagGroupDomain)
                .toList();
    }

    @Override
    public Tag save(Tag tag) {

        TagJpaEntity entity = mapper.toJpaEntity(tag);
        TagJpaEntity saved = tagJpaRepository.save(entity);
        return mapper.toTagDomain(saved);
    }

    @Override
    public Optional<Tag> findTagById(Long id) {

        return tagJpaRepository.findById(id)
                .map(mapper::toTagDomain);
    }

    @Override
    public List<Tag> findByTagGroupId(Long tagGroupId) {

        return tagJpaRepository.findByTagGroupIdAndIsActiveTrueOrderByDisplayOrderAsc(tagGroupId)
                .stream()
                .map(mapper::toTagDomain)
                .toList();
    }
}
