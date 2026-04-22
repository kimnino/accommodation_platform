package com.accommodation.platform.core.supplier.application.port.out;

import java.util.List;
import java.util.Optional;

public interface LoadSupplierPort {

    Optional<SupplierInfo> findByCode(String code);

    record SupplierInfo(Long id, String name, String code) {}

    List<AccommodationMapping> findAccommodationMappingsBySupplierId(Long supplierId);

    record AccommodationMapping(Long accommodationId, String externalAccommodationId) {}

    Optional<Long> resolveRoomOptionId(Long supplierId, String externalRoomId);
}
