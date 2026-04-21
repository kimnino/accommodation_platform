# 검색 성능 전략 설계

> 작성: 2026-04-21

---

## 1. 현재 상태 (Phase 3 구현)

- QueryDSL 동적 쿼리 + 2단계 배치 조회
- 가격 필터는 WHERE절 처리 (Java 후처리 금지)
- 페이징: `Pageable` 기반

**문제**: 검색 요청마다 `inventory`, `room_price` 테이블을 직접 조회 → 트래픽 증가 시 DB 부하

---

## 2. 2단계 배치 조회 (현재 구현)

```
1단계: 조건(지역, 날짜, 인원, 가격범위)으로 accommodation_id 목록 조회 + 페이징
2단계: IN(id_list)로 이미지, 최저가 배치 조회
```

- N+1 방지
- 이미지/가격을 루프 안에서 개별 조회하지 않음

---

## 3. 비정규화 스냅샷 전략 (미구현 — 설계)

### 3-1. accommodation_search_snapshot 테이블

```sql
CREATE TABLE accommodation_search_snapshot (
    id               BIGINT PRIMARY KEY,
    accommodation_id BIGINT NOT NULL,
    region           VARCHAR(100),         -- 검색용 지역 필드 (정규화)
    accommodation_type VARCHAR(50),
    lowest_stay_price  DECIMAL(12,2),      -- 최저 숙박가 (주기적 갱신)
    lowest_hourly_price DECIMAL(12,2),     -- 최저 대실가
    min_capacity     INT,
    max_capacity     INT,
    is_available     BOOLEAN,              -- 재고 있음 여부 (일별 갱신)
    available_from   DATE,
    available_until  DATE,
    updated_at       DATETIME(6),
    INDEX idx_snapshot_region (region),
    INDEX idx_snapshot_type (accommodation_type),
    INDEX idx_snapshot_price (lowest_stay_price),
    INDEX idx_snapshot_available (is_available, available_from)
);
```

### 3-2. 스냅샷 갱신 시점

| 이벤트 | 갱신 대상 |
|--------|----------|
| 가격 등록/수정 (`SetPriceUseCase`) | lowest_stay_price, lowest_hourly_price |
| 재고 등록/수정 (`SetInventoryUseCase`) | is_available, available_from/until |
| 예약 생성/취소 (`CreateReservationUseCase`) | is_available |
| 관리자 숙소 상태 변경 | is_available (ACTIVE만 노출) |

갱신 방법: **도메인 이벤트 → ApplicationEventPublisher → SnapshotUpdateListener**

---

## 4. Redis 캐시 전략 (미구현 — 설계)

### 4-1. 캐시 대상

```
Key: "acc:search:{region}:{checkIn}:{checkOut}:{guestCount}:{type}:{page}"
Value: List<AccommodationSummary> (JSON 직렬화)
TTL: 5분 (검색 결과는 짧은 TTL로 최신성 유지)

Key: "acc:detail:{id}"
Value: AccommodationDetailResponse
TTL: 10분
```

### 4-2. 캐시 무효화

- 숙소 정보 변경(관리자 승인) → `acc:detail:{id}` 삭제
- 재고/가격 변경 → 해당 숙소가 포함될 수 있는 검색 캐시 삭제 (패턴: `acc:search:*`)
  - 주의: 전체 패턴 삭제는 Redis SCAN 사용 (`KEYS *` 금지)

### 4-3. 구현 방법

```java
@Cacheable(value = "accSearch", key = "#criteria.cacheKey()", unless = "#result.isEmpty()")
public Page<AccommodationSummary> search(SearchAccommodationCriteria criteria) { ... }

@CacheEvict(value = {"accSearch", "accDetail"}, allEntries = true)
public void onInventoryChanged(InventoryChangedEvent event) { ... }
```

---

## 5. 읽기/쓰기 분리 (CQRS 확장 — 미구현)

```
쓰기 경로: extranet/admin → core application service → MySQL (primary)
읽기 경로: customer search → Redis 캐시 → MySQL (replica, read-only)
```

Spring DataSource 라우팅:
```java
@ReadOnlyTransactional  // custom annotation
// → AbstractRoutingDataSource가 replica DataSource 선택
```

---

## 6. AccommodationSummary.lowestPrice null 처리 (현재 확인 필요)

```java
// AccommodationSearchJpaAdapter 또는 QueryDSL 쿼리에서
// Inventory/RoomPrice가 없는 숙소의 lowestPrice = null 처리
// → 검색 결과 카드에서 "가격 미설정" 표시 (demo-customer.html에 이미 반영됨)
```

확인 포인트:
- `AccommodationSummary.lowestPrice` 필드가 `BigDecimal`(nullable)인지 확인
- 가격 미설정 숙소가 가격 필터(minPrice/maxPrice)에서 제외되는지 확인

---

## 7. 구현 우선순위

| 단계 | 항목 | 효과 |
|------|------|------|
| 즉시 | 2단계 배치 조회 | ✅ 이미 구현 |
| 단기 | accommodation_search_snapshot | DB 부하 50%+ 감소 예상 |
| 중기 | Redis 캐시 (검색/상세) | 반복 검색 응답시간 10ms 이하 |
| 장기 | 읽기/쓰기 분리 (replica) | 대규모 트래픽 대응 |
