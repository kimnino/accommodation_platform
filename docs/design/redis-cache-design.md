# Redis 캐시 설계 — 숙소 검색 성능 최적화

## 핵심 아이디어

검색 쿼리는 필터+페이징 결과로 **ID 목록만** DB에서 가져오고,
숙소 카드 데이터는 **ID별 Redis 캐시**에서 조립한다.

```
요청
 └─ DB: 필터/정렬/페이징 → [id1, id2, id3, ...]
 └─ Redis: MGET acc:card:{id} × N → 카드 데이터 (정적)
     └─ 캐시 미스 ID → DB 보완 조회 후 Redis SET
 └─ DB: lowestPrice / hasAvailableRoom (실시간, ID 배치)
 └─ 조립 → 응답
```

---

## 캐시 대상 분류

| 캐시 키 패턴 | 대상 데이터 | TTL | 무효화 트리거 |
|---|---|---|---|
| `acc:card:{accommodationId}` | 숙소 카드 (이름·주소·이미지·타입) | 30분 | 파트너 숙소 수정 승인 완료 |
| `acc:card:{accommodationId}:{locale}` | 번역된 숙소 카드 | 30분 | 위와 동일 |
| `tags::{targetType}:{accommodationType}` | 태그 목록 | 1시간 | 관리자 태그 수정 |
| `regions::{accommodationType}` | 지역 목록 | 1시간 | 관리자 지역 수정 |

> **캐시하지 않는 것**: `lowestPrice`, `hasAvailableRoom` — 예약/재고 변동 시 실시간 반영 필수

---

## 숙소 카드 캐시 구조

```java
// Redis에 저장될 캐시 DTO (직렬화 대상)
public record AccommodationCardCache(
    Long id,
    String name,         // 번역 적용된 이름
    String type,
    String fullAddress,
    double latitude,
    double longitude,
    String primaryImagePath
) {}
```

---

## 메서드별 캐시 적용 설계

### 1. 숙소 카드 로드 — `AccommodationCardCacheService`

```java
// 단건: 캐시 우선 조회
@Cacheable(value = "acc:card", key = "#accommodationId + ':' + #locale")
public AccommodationCardCache getCard(Long accommodationId, String locale) {
    // 캐시 미스 시 DB 조회 후 자동 저장
    // LoadAccommodationPort + LoadAccommodationTranslationPort 활용
}

// 배치: 캐시 미스만 DB 보완
public Map<Long, AccommodationCardCache> getCards(List<Long> ids, String locale) {
    // 1. Redis MGET으로 전체 조회
    // 2. 미스된 ID만 DB에서 조회
    // 3. 미스된 항목 Redis에 저장 후 합산 반환
}

// 무효화: 숙소 정보 변경 시
@CacheEvict(value = "acc:card", key = "#accommodationId + ':*'")
public void evictCard(Long accommodationId) {}
```

### 2. 태그 목록 — `CustomerGetTagsService`

```java
@Cacheable(value = "tags", key = "#targetType + ':' + #accommodationType")
public List<TagGroupResponse> getTagGroups(TagTarget targetType, AccommodationType accommodationType) {
    // 현재 구현 그대로, @Cacheable만 추가
}

// 관리자 태그 수정 시 무효화
@CacheEvict(value = "tags", allEntries = true)
public void evictAllTags() {}
```

### 3. 지역 목록 — `AdminManageAccommodationRegionService` / `ExtranetAccommodationRegionService`

```java
@Cacheable(value = "regions", key = "#accommodationType")
public List<AccommodationRegionResponse> getRegions(AccommodationType accommodationType) {
    // 현재 구현 그대로, @Cacheable만 추가
}

@CacheEvict(value = "regions", allEntries = true)
public void evictAllRegions() {}
```

---

## 검색 서비스 변경 포인트

현재 `CustomerSearchAccommodationService`:
```java
// AS-IS: DB에서 AccommodationSummary 전체 조합
Page<AccommodationSummary> page = searchAccommodationPort.search(criteria, pageable);
```

```java
// TO-BE: DB는 ID+동적데이터만, 카드는 캐시에서
Page<Long> idPage = searchAccommodationPort.searchIds(criteria, pageable);   // ID만 조회
Map<Long, AccommodationCardCache> cards = cardCacheService.getCards(idPage.getContent(), locale);
Map<Long, Long> lowestPrices = fetchLowestPrices(idPage.getContent(), criteria); // 실시간
// 조립
```

`SearchAccommodationPort`에 메서드 추가:
```java
// 기존
Page<AccommodationSummary> search(SearchCriteria criteria, Pageable pageable);

// 추가
Page<Long> searchIds(SearchCriteria criteria, Pageable pageable);  // 필터+ID만 반환
```

---

## 무효화 연동 포인트

| 이벤트 | 무효화 대상 |
|---|---|
| 파트너 숙소 수정 승인 (`AdminApproveModificationService`) | `acc:card:{id}:*` |
| 파트너 숙소 직접 수정 (`ExtranetUpdateAccommodationService`) | `acc:card:{id}:*` |
| 관리자 태그 생성/수정/삭제 | `tags::*` |
| 관리자 지역 생성/수정/삭제 | `regions::*` |

---

## 의존성 추가 시 (구현 시점)

```gradle
// build.gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

```yaml
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
  cache:
    type: redis
    redis:
      time-to-live: 1800000  # 30분 (ms)
```

```java
// CacheConfig.java
@EnableCaching
@Configuration
public class CacheConfig {
    // RedisCacheManager 설정
    // 캐시별 TTL 개별 지정
    // Jackson2JsonRedisSerializer 직렬화 설정
}
```
