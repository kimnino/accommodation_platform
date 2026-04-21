package com.accommodation.platform.core.tag.application.port.out;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

public interface LoadTagGroupPort {

    Optional<TagGroup> findById(Long id);

    List<TagGroup> findAll();

    List<TagGroup> findByTargetTypeAndAccommodationType(TagTarget targetType, AccommodationType accommodationType);

    List<TagGroup> findBySupplierIdAndTargetTypeAndAccommodationType(Long supplierId, TagTarget targetType, AccommodationType accommodationType);
}
