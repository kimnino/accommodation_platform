package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.price.adapter.out.persistence.QRoomPriceJpaEntity;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.room.adapter.out.persistence.QRoomJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.QRoomOptionJpaEntity;
import com.accommodation.platform.core.tag.adapter.out.persistence.QAccommodationTagJpaEntity;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.SearchCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccommodationSearchJpaAdapter implements SearchAccommodationPort {

    private final JPAQueryFactory queryFactory;
    private final AccommodationRegionJpaRepository regionRepository;

    @Override
    public Page<Long> searchIds(SearchCriteria criteria, Pageable pageable) {

        QAccommodationJpaEntity a = QAccommodationJpaEntity.accommodationJpaEntity;
        BooleanBuilder where = buildWhereClause(a, criteria);

        List<Long> ids = queryFactory
                .select(a.id)
                .from(a)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(a.id.desc())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Long total = queryFactory
                .select(a.count())
                .from(a)
                .where(where)
                .fetchOne();

        return new PageImpl<>(ids, pageable, total != null ? total : 0);
    }

    @Override
    public Map<Long, Long> loadLowestPrices(List<Long> accommodationIds, SearchCriteria criteria) {

        return fetchLowestPrices(accommodationIds, criteria);
    }

    private BooleanBuilder buildWhereClause(QAccommodationJpaEntity a, SearchCriteria criteria) {

        QRoomJpaEntity r = QRoomJpaEntity.roomJpaEntity;
        QRoomOptionJpaEntity ro = QRoomOptionJpaEntity.roomOptionJpaEntity;
        QRoomPriceJpaEntity p = QRoomPriceJpaEntity.roomPriceJpaEntity;
        QAccommodationTagJpaEntity at = QAccommodationTagJpaEntity.accommodationTagJpaEntity;

        BooleanBuilder where = new BooleanBuilder();
        where.and(a.status.eq(AccommodationStatus.ACTIVE));

        if (criteria.regionId() != null) {
            List<Long> subtreeIds = resolveRegionSubtreeIds(criteria.regionId());
            where.and(a.regionId.in(subtreeIds));
        }
        if (criteria.accommodationType() != null) {
            where.and(a.type.eq(criteria.accommodationType()));
        }
        if (criteria.guestCount() > 0) {
            where.and(JPAExpressions.selectOne()
                    .from(r)
                    .where(r.accommodationId.eq(a.id), r.maxCapacity.goe(criteria.guestCount()))
                    .exists());
        }
        if (criteria.tagIds() != null && !criteria.tagIds().isEmpty()) {
            where.and(JPAExpressions.selectOne()
                    .from(at)
                    .where(at.accommodationId.eq(a.id), at.tagId.in(criteria.tagIds()))
                    .exists());
        }

        // 가격 범위 필터 — EXISTS 서브쿼리로 해당 가격대 숙소만 조회
        if (criteria.minPrice() != null || criteria.maxPrice() != null) {
            BooleanBuilder priceCondition = new BooleanBuilder()
                    .and(r.accommodationId.eq(a.id))
                    .and(ro.roomId.eq(r.id))
                    .and(p.roomOptionId.eq(ro.id))
                    .and(p.priceType.eq(PriceType.STAY));

            if (criteria.checkInDate() != null && criteria.checkOutDate() != null) {
                priceCondition.and(p.date.between(criteria.checkInDate(), criteria.checkOutDate().minusDays(1)));
            }
            if (criteria.minPrice() != null) {
                priceCondition.and(p.sellingPrice.goe(BigDecimal.valueOf(criteria.minPrice())));
            }
            if (criteria.maxPrice() != null) {
                priceCondition.and(p.sellingPrice.loe(BigDecimal.valueOf(criteria.maxPrice())));
            }

            where.and(JPAExpressions.selectOne()
                    .from(p)
                    .join(ro).on(ro.id.eq(p.roomOptionId))
                    .join(r).on(r.id.eq(ro.roomId))
                    .where(priceCondition)
                    .exists());
        }

        return where;
    }

    /**
     * 선택한 지역 + 모든 하위 지역 ID를 재귀적으로 수집.
     * 지역 데이터는 소량이므로 전체 로드 후 in-memory 탐색.
     */
    private List<Long> resolveRegionSubtreeIds(Long rootRegionId) {
        List<AccommodationRegionJpaEntity> all = regionRepository.findAll();
        List<Long> result = new ArrayList<>();
        collectSubtreeIds(rootRegionId, all, result);
        return result;
    }

    private void collectSubtreeIds(Long parentId, List<AccommodationRegionJpaEntity> all, List<Long> result) {
        result.add(parentId);
        all.stream()
                .filter(r -> parentId.equals(r.getParentId()))
                .forEach(r -> collectSubtreeIds(r.getId(), all, result));
    }

    /**
     * 숙소별 최저가 배치 조회.
     * room → room_option → room_price 조인 후 MIN(selling_price) GROUP BY accommodation_id.
     */
    private Map<Long, Long> fetchLowestPrices(List<Long> accommodationIds, SearchCriteria criteria) {

        QRoomJpaEntity r = QRoomJpaEntity.roomJpaEntity;
        QRoomOptionJpaEntity ro = QRoomOptionJpaEntity.roomOptionJpaEntity;
        QRoomPriceJpaEntity p = QRoomPriceJpaEntity.roomPriceJpaEntity;

        var query = queryFactory
                .select(r.accommodationId, p.sellingPrice.min())
                .from(p)
                .join(ro).on(ro.id.eq(p.roomOptionId))
                .join(r).on(r.id.eq(ro.roomId))
                .where(r.accommodationId.in(accommodationIds),
                        p.priceType.eq(PriceType.STAY));

        if (criteria.checkInDate() != null && criteria.checkOutDate() != null) {
            query.where(p.date.between(criteria.checkInDate(), criteria.checkOutDate().minusDays(1)));
        }

        List<Tuple> prices = query
                .groupBy(r.accommodationId)
                .fetch();

        return prices.stream()
                .collect(Collectors.toMap(
                        t -> t.get(r.accommodationId),
                        t -> {
                            BigDecimal min = t.get(p.sellingPrice.min());
                            return min != null ? min.longValue() : 0L;
                        }));
    }
}
