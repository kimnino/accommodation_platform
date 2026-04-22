package com.accommodation.platform.core.accommodation.application.port.out;

import java.util.List;

public interface LoadAccommodationCardPort {

    /**
     * 숙소 ID 목록으로 카드 데이터 배치 조회.
     * locale별로 캐시 키를 분리 — Redis 도입 시 acc:card:{id}:{locale} 키 사용.
     * 현재는 DB에서 직접 조회하며, 추후 Redis 어댑터로 교체 예정.
     */
    List<AccommodationCard> findByIds(List<Long> ids, String locale);

    record AccommodationCard(
            Long id,
            String name,
            String type,
            String fullAddress,
            double latitude,
            double longitude,
            String primaryImagePath
    ) {}
}
