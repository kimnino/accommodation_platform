package com.accommodation.platform.core.accommodation.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchAccommodationPort {

    Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable);

    /** 필터/정렬/페이징 결과로 숙소 ID만 반환 — 카드 데이터는 별도 포트에서 조회 */
    Page<Long> searchIds(SearchCriteria criteria, Pageable pageable);

    /** 숙소별 최저가 배치 조회 (날짜 조건 포함, 실시간) */
    Map<Long, Long> loadLowestPrices(List<Long> accommodationIds, SearchCriteria criteria);

    record SearchCriteria(
            Long regionId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int guestCount,
            String accommodationType,
            Long minPrice,
            Long maxPrice,
            List<Long> tagIds,
            String sort
    ) {}

    record AccommodationSummary(
            Long id,
            String name,
            String type,
            String status,
            String fullAddress,
            double latitude,
            double longitude,
            String locationDescription,
            String checkInTime,
            String checkOutTime,
            String primaryImagePath,
            Long lowestPrice,
            boolean hasAvailableRoom
    ) {}
}
