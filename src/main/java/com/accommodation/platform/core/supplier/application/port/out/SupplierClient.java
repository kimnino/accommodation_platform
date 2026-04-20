package com.accommodation.platform.core.supplier.application.port.out;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.supplier.domain.model.CanonicalAccommodation;
import com.accommodation.platform.core.supplier.domain.model.CanonicalPrice;

/**
 * 외부 공급사 API 추상화.
 * 공급사별로 이 인터페이스를 구현하여 데이터를 Canonical Model로 변환해 반환.
 */
public interface SupplierClient {

    String getSupplierCode();

    List<CanonicalAccommodation> fetchAccommodations();

    List<CanonicalPrice> fetchPrices(String externalAccommodationId, LocalDate startDate, LocalDate endDate);
}
