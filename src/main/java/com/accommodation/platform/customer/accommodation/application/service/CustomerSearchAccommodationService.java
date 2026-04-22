package com.accommodation.platform.customer.accommodation.application.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationCardPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationCardPort.AccommodationCard;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.AccommodationSummary;
import com.accommodation.platform.core.accommodation.application.port.out.SearchAccommodationPort.SearchCriteria;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerSearchAccommodationQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerSearchAccommodationService implements CustomerSearchAccommodationQuery {

    private final SearchAccommodationPort searchAccommodationPort;
    private final LoadAccommodationCardPort loadAccommodationCardPort;  // 추후 Redis 어댑터로 교체

    @Override
    public Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable) {

        String locale = LocaleContextHolder.getLocale().getLanguage();

        // 1단계: DB — 필터/정렬/페이징, ID만 반환
        Page<Long> idPage = searchAccommodationPort.searchIds(criteria, pageable);
        if (idPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> ids = idPage.getContent();

        // 2단계: 카드 데이터 조회 (번역 포함, locale별 캐시 분리)
        // 현재는 DB 직접 조회 — Redis 도입 시 이 포트 구현체만 교체
        Map<Long, AccommodationCard> cardMap = loadAccommodationCardPort.findByIds(ids, locale).stream()
                .collect(Collectors.toMap(AccommodationCard::id, c -> c));

        // 3단계: 최저가 실시간 조회
        Map<Long, Long> lowestPrices = searchAccommodationPort.loadLowestPrices(ids, criteria);

        // 4단계: 조립 — searchIds 반환 순서 유지
        List<AccommodationSummary> summaries = ids.stream()
                .filter(cardMap::containsKey)
                .map(id -> {
                    AccommodationCard card = cardMap.get(id);
                    return new AccommodationSummary(
                            id, card.name(), card.type(),
                            null,
                            card.fullAddress(),
                            card.latitude(), card.longitude(),
                            null, null, null,
                            card.primaryImagePath(),
                            lowestPrices.get(id),
                            lowestPrices.containsKey(id));
                })
                .toList();

        return new PageImpl<>(summaries, pageable, idPage.getTotalElements());
    }
}
