package com.accommodation.platform.core.tag.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<TagJpaEntity, Long> {

    List<TagJpaEntity> findByTagGroupIdAndIsActiveTrueOrderByDisplayOrderAsc(Long tagGroupId);
}
