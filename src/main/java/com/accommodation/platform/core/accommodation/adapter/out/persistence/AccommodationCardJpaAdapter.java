package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationCardPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationTranslationPort;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 숙소 카드 데이터 DB 조회 어댑터.
 * LoadAccommodationCardPort 구현체 — 추후 Redis 캐시 어댑터로 교체 예정.
 * locale별로 번역된 이름/주소를 포함해 카드를 구성 (Redis 키: acc:card:{id}:{locale}).
 */
@Repository
@RequiredArgsConstructor
public class AccommodationCardJpaAdapter implements LoadAccommodationCardPort {

    private final AccommodationJpaRepository jpaRepository;
    private final AccommodationTranslationJpaRepository translationJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<AccommodationCard> findByIds(List<Long> ids, String locale) {

        if (ids.isEmpty()) {
            return List.of();
        }

        List<AccommodationJpaEntity> accommodations = jpaRepository.findAllById(ids);
        Map<Long, String> primaryImages = fetchPrimaryImages(ids);

        // 번역 배치 로드 (ko면 스킵)
        Map<Long, AccommodationTranslationJpaEntity> translations = "ko".equals(locale)
                ? Map.of()
                : translationJpaRepository.findByAccommodationIdInAndLocale(ids, locale).stream()
                        .collect(Collectors.toMap(
                                AccommodationTranslationJpaEntity::getAccommodationId, t -> t));

        return accommodations.stream()
                .map(acc -> {
                    AccommodationTranslationJpaEntity tr = translations.get(acc.getId());
                    String name = (tr != null && tr.getName() != null && !tr.getName().isBlank())
                            ? tr.getName() : acc.getName();
                    String fullAddress = (tr != null && tr.getFullAddress() != null && !tr.getFullAddress().isBlank())
                            ? tr.getFullAddress() : acc.getFullAddress();
                    return new AccommodationCard(
                            acc.getId(), name, acc.getType().name(),
                            fullAddress, acc.getLatitude(), acc.getLongitude(),
                            primaryImages.get(acc.getId()));
                })
                .toList();
    }

    private Map<Long, String> fetchPrimaryImages(List<Long> ids) {

        QAccommodationImageJpaEntity img = QAccommodationImageJpaEntity.accommodationImageJpaEntity;

        List<Tuple> rows = queryFactory
                .select(img.accommodationId, img.relativePath)
                .from(img)
                .where(img.accommodationId.in(ids), img.isPrimary.eq(true))
                .fetch();

        return rows.stream()
                .collect(Collectors.toMap(
                        t -> t.get(img.accommodationId),
                        t -> t.get(img.relativePath),
                        (a, b) -> a));
    }
}
