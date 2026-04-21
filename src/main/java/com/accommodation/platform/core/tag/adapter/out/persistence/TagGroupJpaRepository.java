package com.accommodation.platform.core.tag.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;

public interface TagGroupJpaRepository extends JpaRepository<TagGroupJpaEntity, Long> {

    @Query("SELECT tg FROM TagGroupJpaEntity tg " +
            "WHERE tg.targetType = :targetType " +
            "AND (tg.accommodationType IS NULL OR tg.accommodationType = :accommodationType) " +
            "AND tg.supplierId IS NULL " +
            "AND tg.isActive = true " +
            "ORDER BY tg.displayOrder ASC")
    List<TagGroupJpaEntity> findByTargetTypeAndAccommodationType(
            TagTarget targetType, AccommodationType accommodationType);

    @Query("SELECT tg FROM TagGroupJpaEntity tg " +
            "WHERE tg.supplierId = :supplierId " +
            "AND tg.targetType = :targetType " +
            "AND (tg.accommodationType IS NULL OR tg.accommodationType = :accommodationType) " +
            "AND tg.isActive = true " +
            "ORDER BY tg.displayOrder ASC")
    List<TagGroupJpaEntity> findBySupplierIdAndTargetTypeAndAccommodationType(
            Long supplierId, TagTarget targetType, AccommodationType accommodationType);
}
