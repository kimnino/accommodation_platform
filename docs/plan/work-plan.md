# OTA 숙박 플랫폼 작업계획서

## 구현 순서 요약

| Phase | 내용 | 상태 |
|-------|------|------|
| 0 | 공통 인프라 (ApiResponse, 예외, BaseEntity, 테스트 기반) | ✅ 완료 |
| 1 | 숙소/객실 등록 + 태그 시스템 | ✅ 완료 (피드백 6차 반영) |
| 2 | 재고/요금 설정 (Inventory + Price) | ✅ 완료 (피드백 3차 반영) |
| 3 | 검색 (Customer Search) | ✅ 완료 (피드백 1차 반영) |
| 4 | 예약 + 동시성 (Reservation) | ✅ 완료 (피드백 2차 반영) |
| 5 | Supplier 연동 (MINHYUK_HOUSE) | ✅ 완료 (피드백 1차 반영) |
| 6 | 설계 중심 도메인 (Member, Coupon, Payment, Review, Wishlist) | ✅ 완료 |
| 7 | 횡단 관심사 (Security, i18n, Image, Logging) | ✅ 완료 |
| 8 | Demo UI & 실행 환경 (HTML 12페이지, 44개 API) | ✅ 완료 (버그 수정 완료) |
| 9 | 최종 점검 (코드 리뷰, 버그 수정, 설계 문서화) | ✅ 완료 (Demo UI QA만 남음) |

> Phase 6, 7은 병렬 진행 가능

---

## 브랜치 전략

각 Phase별 feature 브랜치에서 작업 후 main에 머지한다. Phase 0은 모든 Phase의 기반이므로 가장 먼저 머지한다.

| 브랜치명 | Phase | 범위 | 비고 |
|----------|-------|------|------|
| `feat/phase0-common-infra` | 0 | 0-1 ~ 0-6 전체 | 모든 Phase의 기반. 최우선 머지 |
| `feat/phase1-accommodation` | 1 | 1-1 ~ 1-3, 1-5 ~ 1-7 | Accommodation + Room 도메인, Extranet/Admin 채널 |
| `feat/phase1-tag-system` | 1 | 1-4 | 동적 태그 시스템 (독립 분리) |
| `feat/phase2-inventory-price` | 2 | 2-1 ~ 2-7 전체 | Inventory + Price 도메인 |
| `feat/phase3-search` | 3 | 3-1 ~ 3-4 전체 | 고객 숙소 검색 |
| `feat/phase4-reservation` | 4 | 4-1 ~ 4-7 전체 | 예약 + 동시성 처리 |
| `feat/phase5-supplier` | 5 | 5-1 ~ 5-3 전체 | Supplier 연동 |
| `feat/phase6-skeleton-domains` | 6 | 6-1 ~ 6-5 전체 | 설계 중심 도메인 (ERD + 스켈레톤) |
| `feat/phase7-cross-cutting` | 7 | 7-1 ~ 7-4 전체 | 횡단 관심사 |
| `feat/phase8-demo-ui` | 8 | HTML 12페이지 + 실행 환경 | Demo UI, Docker, 샘플 데이터 |

### 머지 순서

```
main ← feat/phase0-common-infra
     ← feat/phase1-accommodation
     ← feat/phase1-tag-system
     ← feat/phase2-inventory-price
     ← feat/phase3-search
     ← feat/phase4-reservation
     ← feat/phase5-supplier
     ← feat/phase6-skeleton-domains  (Phase 6, 7 병렬 가능)
     ← feat/phase7-cross-cutting
```

> 작업 요청 시 현재 브랜치가 해당 Phase 브랜치와 다르면, 올바른 브랜치로 checkout 후 작업한다.

---

## Phase 0: 프로젝트 기반 구조

### 0-1. build.gradle 의존성 보강
- `spring-boot-starter-validation`
- `org.testcontainers:mysql` + `org.testcontainers:junit-jupiter`
- `spring-boot-starter-security` (Phase 7에서 활성화)

### 0-2. application.yaml 설정
- `spring.jackson.property-naming-strategy: SNAKE_CASE`
- `spring.jpa.hibernate.ddl-auto: validate`
- `spring.jpa.open-in-view: false`
- 로깅 패턴 (trace_id MDC 포함)

### 0-3. 공통 응답 / 예외 처리

위치: `com.accommodation.platform.common`

| 파일 | 설명 |
|------|------|
| `response/ApiResponse.java` | record. status, data, error, timestamp |
| `response/ErrorDetail.java` | record. code, message, fieldErrors |
| `response/FieldError.java` | record. field, message |
| `exception/BusinessException.java` | RuntimeException 상속, ErrorCode 보유 |
| `exception/ErrorCode.java` | Enum. HTTP status + 비즈니스 에러 코드 |
| `exception/GlobalExceptionHandler.java` | @RestControllerAdvice 전역 예외 처리 |

### 0-4. 공통 도메인 베이스

| 위치 | 파일 | 설명 |
|------|------|------|
| `common.domain` | `BaseEntity.java` | createdAt(Instant), updatedAt(Instant). 순수 Java |
| `common.domain` | `SoftDeletable.java` | isDeleted, deletedAt 필드 인터페이스 |
| `common.adapter.out.persistence` | `BaseJpaEntity.java` | @MappedSuperclass. 공통 컬럼 + @PrePersist/@PreUpdate |

### 0-5. 설정 클래스

| 파일 | 설명 |
|------|------|
| `common/config/JacksonConfig.java` | SNAKE_CASE, Instant 직렬화 |
| `common/config/WebMvcConfig.java` | CORS 등 |

### 0-6. 테스트 인프라

| 파일 | 설명 |
|------|------|
| `IntegrationTestBase.java` | @SpringBootTest + Testcontainers MySQL 공통 베이스 |
| `application-test.yaml` | 테스트 전용 DB 설정 |

---

## Phase 1: 숙소/객실 등록

### 1-1. Accommodation 도메인 모델

위치: `core.accommodation.domain.model`

| 파일 | 설명 |
|------|------|
| `Accommodation.java` | 엔티티. name, type, address, latitude/longitude, status, tags(List), images, checkInTime/checkOutTime |
| `AccommodationType.java` | Enum: HOTEL, RESORT, PENSION, POOL_VILLA, MOTEL, GUEST_HOUSE |
| `AccommodationStatus.java` | Enum: PENDING, ACTIVE, SUSPENDED, CLOSED |
| `Address.java` | VO. region, city, district, detail, zipCode |
| `AccommodationImage.java` | VO. url, category, displayOrder, isPrimary |
| `ImageCategory.java` | Enum: EXTERIOR, LOBBY, ROOM, FACILITY, ETC |

### 1-2. Accommodation 포트 & 어댑터 (core)

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/out | `AccommodationRepository.java` | save, findById, findByPartnerId |
| port/out | `AccommodationImageStorage.java` | 이미지 업로드/삭제 추상화 |
| persistence | `AccommodationJpaEntity.java` | JPA 엔티티 |
| persistence | `AccommodationImageJpaEntity.java` | 이미지 JPA 엔티티 |
| persistence | `AccommodationJpaRepository.java` | Spring Data JPA |
| persistence | `AccommodationJpaAdapter.java` | Repository 구현체 |
| persistence | `AccommodationMapper.java` | JpaEntity <-> 도메인 모델 |

### 1-3. Extranet 채널 - 숙소 등록/수정

위치: `extranet.accommodation`

| 레이어 | 파일 |
|--------|------|
| port/in | `ExtranetRegisterAccommodationUseCase.java` |
| port/in | `ExtranetUpdateAccommodationUseCase.java` |
| port/in | `ExtranetGetAccommodationQuery.java` |
| service | `ExtranetRegisterAccommodationService.java` |
| service | `ExtranetUpdateAccommodationService.java` |
| service | `ExtranetGetAccommodationService.java` |
| web | `ExtranetAccommodationController.java` — `/api/v1/extranet/accommodations` |
| web | `RegisterAccommodationRequest.java`, `UpdateAccommodationRequest.java`, `AccommodationDetailResponse.java` |

### 1-4. 동적 태그 시스템 (Tag)

위치: `core.tag.domain.model`

> 기존 `Facility` Enum 대체. 관리자가 코드 배포 없이 태그 그룹/태그를 자유롭게 추가·삭제.

| 파일 | 설명 |
|------|------|
| `TagGroup.java` | 엔티티. name, displayOrder, targetType(ACCOMMODATION/ROOM), accommodationType(HOTEL/MOTEL/ALL 등), isActive |
| `Tag.java` | 엔티티. tagGroupId, name, displayOrder, isActive |
| `TagTarget.java` | Enum: ACCOMMODATION, ROOM |

```
TagGroup (취향, accommodationType: HOTEL)
  ├─ Tag: #가족여행숙소
  ├─ Tag: #드라마촬영지
  └─ Tag: #스파

TagGroup (공용시설, accommodationType: ALL)
  ├─ Tag: 수영장
  ├─ Tag: 사우나
  └─ Tag: 피트니스
```

위치: `core.tag`

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/out | `TagGroupRepository.java` | 태그 그룹 CRUD |
| port/out | `TagRepository.java` | 태그 CRUD |
| persistence | `TagGroupJpaEntity.java`, `TagJpaEntity.java` | JPA 엔티티 |
| persistence | `AccommodationTagJpaEntity.java` | 숙소-태그 매핑 (accommodationId, tagId) |
| persistence | `RoomTagJpaEntity.java` | 객실-태그 매핑 (roomId, tagId) |
| persistence | `TagJpaAdapter.java` | Repository 구현체 |

위치: `admin.tag`

| 레이어 | 파일 |
|--------|------|
| port/in | `AdminCreateTagGroupUseCase.java`, `AdminManageTagUseCase.java` |
| service | `AdminCreateTagGroupService.java`, `AdminManageTagService.java` |
| web | `AdminTagController.java` — `/api/v1/admin/tag-groups`, `/api/v1/admin/tag-groups/{groupId}/tags` |

위치: `extranet.tag`

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/in | `ExtranetGetAvailableTagQuery.java` | 내 숙소유형에 맞는 태그 그룹/태그 목록 조회 |
| service | `ExtranetGetAvailableTagService.java` | accommodationType 기반 필터링 |

### 1-5. Admin 채널 - 숙소 승인/관리

위치: `admin.accommodation`

| 레이어 | 파일 |
|--------|------|
| port/in | `AdminApproveAccommodationUseCase.java` |
| port/in | `AdminListAccommodationQuery.java` |
| service | `AdminApproveAccommodationService.java` |
| service | `AdminListAccommodationService.java` |
| web | `AdminAccommodationController.java` — `/api/v1/admin/accommodations` |

### 1-5. Room 도메인 모델

위치: `core.room.domain.model`

| 파일 | 설명 |
|------|------|
| `Room.java` | 엔티티. accommodationId, name, roomType, standardCapacity, maxCapacity, tags(List), images, status |
| `RoomType.java` | Enum: STANDARD, DELUXE, SUITE, FAMILY, ONDOL |
| `RoomStatus.java` | Enum: ACTIVE, INACTIVE |
| `RoomOption.java` | 엔티티. roomId, name, cancellationPolicy, breakfastIncluded, additionalPrice(BigDecimal). **1객실:N옵션** |
| `CancellationPolicy.java` | Enum: FREE_CANCELLATION, NON_REFUNDABLE, PARTIAL_REFUND |

### 1-6. Room 포트 & 어댑터 (core)

| 레이어 | 파일 |
|--------|------|
| port/out | `RoomRepository.java`, `RoomOptionRepository.java` |
| persistence | `RoomJpaEntity.java`, `RoomOptionJpaEntity.java` |
| persistence | `RoomJpaRepository.java`, `RoomJpaAdapter.java`, `RoomMapper.java` |

### 1-7. Extranet 채널 - 객실/옵션 등록

위치: `extranet.room`

| 레이어 | 파일 |
|--------|------|
| port/in | `ExtranetRegisterRoomUseCase.java`, `ExtranetRegisterRoomOptionUseCase.java`, `ExtranetGetRoomQuery.java` |
| service | `ExtranetRegisterRoomService.java` (숙소 소유권 검증 포함), `ExtranetRegisterRoomOptionService.java`, `ExtranetGetRoomService.java` |
| web | `ExtranetRoomController.java` — `/api/v1/extranet/accommodations/{accommodationId}/rooms` |

### Phase 1 테스트

- Accommodation 도메인 모델 단위 테스트 (상태 전환 invariant)
- ExtranetRegisterAccommodationService 단위 테스트 (Mockito)
- AccommodationJpaAdapter 통합 테스트 (Testcontainers)
- ExtranetAccommodationController REST Docs 테스트

---

## Phase 2: 재고 + 요금 설정

### 2-1. Inventory 도메인 모델

위치: `core.inventory.domain.model`

| 파일 | 설명 |
|------|------|
| `Inventory.java` | 엔티티. roomOptionId, date(LocalDate), totalQuantity, remainingQuantity, status. 불변식: `remaining >= 0` |
| `InventoryStatus.java` | Enum: AVAILABLE, SOLD_OUT, CLOSED |
| `TimeSlotInventory.java` | 대실용. date, startTime(LocalTime), endTime(LocalTime), totalQuantity, remainingQuantity |

### 2-2. Inventory 도메인 서비스 & 이벤트

| 파일 | 설명 |
|------|------|
| `domain/service/InventoryDomainService.java` | 날짜 범위 재고 가용성 판단, 연박 검증, **숙박/대실 상호 배타 동기화** |
| `domain/event/InventoryDepletedEvent.java` | 재고 소진 시 발행 |
| `domain/event/InventoryRestoredEvent.java` | 취소로 재고 복구 시 |

#### 숙박/대실 재고 충돌 방지 (Critical)

물리 객실 수량(`Room.totalQuantity`)을 상위 기준으로, Inventory와 TimeSlotInventory가 상호 참조하는 구조:

```
물리 객실 A (1개)
  ├─ 숙박 예약 시 → 해당 날짜의 모든 대실 슬롯 자동 마감
  └─ 대실 예약 시 → 해당 날짜의 숙박 가능 여부 재계산
      └─ 청소 시간(Buffer Time) 고려: 대실 슬롯 사이 최소 정비시간 확보
```

- `InventoryDomainService`에서 숙박/대실 상태 동기화 로직 구현
- 대실 슬롯 예약 시 `bufferMinutes` (숙소별 설정) 만큼 전후 슬롯 차단
- 하루 중 대실로 인해 연속 가용 시간이 부족하면 숙박 불가 판정

### 2-3. Inventory 포트 & 어댑터

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/out | `InventoryRepository.java` | **`findWithLock` 메서드 포함** (동시성 제어) |
| persistence | `InventoryJpaEntity.java` | 비관적 락 설정 |
| persistence | `InventoryJpaRepository.java` | `@Lock(PESSIMISTIC_WRITE)` 메서드 |
| persistence | `InventoryJpaAdapter.java`, `InventoryMapper.java` | |

### 2-4. Price 도메인 모델

위치: `core.price.domain.model`

| 파일 | 설명 |
|------|------|
| `RoomPrice.java` | 엔티티. roomOptionId, date(LocalDate), basePrice(BigDecimal), sellingPrice(BigDecimal), taxIncluded(boolean) |
| `domain/service/PriceDomainService.java` | 박수 기반 총 가격 합산 계산, VAT 계산 |

#### VAT(부가세) 처리

- `RoomPrice`에 `taxIncluded` 플래그로 세금 포함/불포함 구분
- `PriceDomainService`에서 국가별 세율 적용 로직 (글로벌 확장 대비)
- 초기에는 한국 VAT 10% 고정, 향후 국가별 세율 테이블로 확장

### 2-5. Price 포트 & 어댑터

| 레이어 | 파일 |
|--------|------|
| port/out | `RoomPriceRepository.java` |
| persistence | `RoomPriceJpaEntity.java`, `RoomPriceJpaRepository.java`, `RoomPriceJpaAdapter.java`, `RoomPriceMapper.java` |

### 2-6. Extranet 채널 - 재고/요금 설정

| 도메인 | 포트 | 컨트롤러 |
|--------|------|----------|
| inventory | `ExtranetSetInventoryUseCase`, `ExtranetGetInventoryQuery` | `ExtranetInventoryController` — `/api/v1/extranet/rooms/{roomOptionId}/inventories` |
| price | `ExtranetSetPriceUseCase`, `ExtranetGetPriceQuery` | `ExtranetPriceController` — `/api/v1/extranet/rooms/{roomOptionId}/prices` |

### 2-7. Admin 채널 - 요금 조정

| 파일 |
|------|
| `AdminAdjustPriceUseCase`, `AdminAdjustPriceService`, `AdminPriceController` |

### Phase 2 테스트

- Inventory 도메인 단위 테스트 (decrease 시 0 미만 방지)
- PriceDomainService 단위 테스트 (박수 합산)
- InventoryJpaAdapter 통합 테스트 (비관적 락 동작)

---

## Phase 3: 검색

### 3-1. Customer 채널 - 숙소 검색

위치: `customer.accommodation`

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/in | `CustomerSearchAccommodationQuery.java` | `search(criteria): Page<AccommodationSummary>` |
| port/in | `CustomerGetAccommodationDetailQuery.java` | 숙소 상세 (객실+옵션+가격) |
| service | `CustomerSearchAccommodationService.java` | Accommodation + Inventory + Price 조합 조회 |
| service | `CustomerGetAccommodationDetailService.java` | 날짜 범위 가용 객실+가격 |
| web | `CustomerAccommodationController.java` — `/api/v1/accommodations` |
| web | `SearchAccommodationRequest.java` — region, checkInDate, checkOutDate, guestCount, filters, sort |
| web | `AccommodationSummaryResponse.java` — 리스트용 (이름, 대표이미지, 최저가, 평점) |
| web | `AccommodationDetailResponse.java` — 상세 (객실 목록, 옵션별 가격, 잔여 재고) |

### 3-2. 검색 전용 Repository (core)

| 파일 | 설명 |
|------|------|
| `AccommodationSearchRepository.java` | 검색 전용 Outbound Port (CQRS 분리) |
| `AccommodationSearchJpaAdapter.java` | QueryDSL 또는 JPQL 동적 검색 구현 |

### 3-3. 검색 기능

- **필터**: 가격 범위, 숙소유형, **동적 태그 기반** (숙소유형에 따라 필터 항목 자동 변경), 예약가능 여부 (재고 join)
- **정렬**: 추천순(기본), 가격 낮은/높은순, 평점순
- **페이징**: Offset 기반 (초기)

### 3-4. 검색 성능 최적화 전략

실시간으로 Inventory + Price를 수만 건 join하면 응답 속도 저하 → 아래 전략 적용:

**1차: QueryDSL 최적화**
- N+1 방지를 위한 fetchJoin 전략 명시
- 커버링 인덱스 활용 (숙소 지역, 날짜, 상태)

**2차: 검색용 비정규화 (Denormalization)**
- 검색 전용 스냅샷 테이블 또는 Materialized View 개념 도입
- 숙소별 최저가, 잔여 재고 여부를 미리 계산하여 캐싱
- 재고/가격 변경 시 이벤트 기반으로 스냅샷 갱신

**3차: Redis 캐시**
- 인기 검색 조건(지역+날짜)에 대한 결과 캐싱
- TTL 기반 자동 만료 + 재고 변경 이벤트 시 캐시 무효화

### Phase 3 테스트

- CustomerSearchAccommodationService 단위 테스트
- AccommodationSearchJpaAdapter 통합 테스트 (복합 조건)
- 검색 API REST Docs 테스트

---

## Phase 4: 예약 + 동시성 처리

### 4-1. Reservation 도메인 모델

위치: `core.reservation.domain.model`

| 파일 | 설명 |
|------|------|
| `Reservation.java` | 엔티티. reservationNumber, memberId, roomOptionId, accommodationId, checkInDate, checkOutDate, guestInfo(GuestInfo), totalPrice(BigDecimal), status, reservationType |
| `ReservationStatus.java` | Enum: PENDING, **PAYMENT_WAITING**, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW |
| `ReservationType.java` | Enum: STAY(숙박), HOURLY(대실) |
| `GuestInfo.java` | VO. name, phone, email |

### 4-2. 도메인 서비스 & 이벤트

| 파일 | 설명 |
|------|------|
| `domain/service/ReservationDomainService.java` | 예약 생성 규칙 검증, 취소 환불 계산 |
| `domain/event/ReservationCreatedEvent.java` | |
| `domain/event/ReservationConfirmedEvent.java` | |
| `domain/event/ReservationCancelledEvent.java` | 재고 복구 트리거 |

### 4-3. Reservation 포트 & 어댑터

| 레이어 | 파일 |
|--------|------|
| port/out | `ReservationRepository.java` |
| persistence | `ReservationJpaEntity.java`, `ReservationJpaRepository.java`, `ReservationJpaAdapter.java`, `ReservationMapper.java` |

### 4-4. Customer 채널 - 예약 생성/취소

위치: `customer.reservation`

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/in | `CustomerCreateReservationUseCase.java` | 재고 확인 → 재고 선점(hold) → PAYMENT_WAITING → 결제 완료 → 확정 |
| port/in | `CustomerCancelReservationUseCase.java` | 취소 정책 기반 환불 + 재고 복구 |
| port/in | `CustomerGetReservationQuery.java` | 내 예약 목록/상세 |
| service | `CustomerCreateReservationService.java` | **핵심**. @Transactional + InventoryRepository.findWithLock() |
| service | `CustomerCancelReservationService.java` | 상태 변경 + 재고 복구 + 이벤트 발행 |
| web | `CustomerReservationController.java` — `/api/v1/reservations` |

### 4-5. Extranet 채널 - 예약 관리

위치: `extranet.reservation`

| 레이어 | 파일 |
|--------|------|
| port/in | `ExtranetConfirmReservationUseCase.java` (수동 확정) |
| port/in | `ExtranetGetReservationQuery.java` |
| service | `ExtranetConfirmReservationService.java`, `ExtranetGetReservationService.java` |
| web | `ExtranetReservationController.java` — `/api/v1/extranet/reservations` |

### 4-6. 재고 선점(Soft-lock) + Hold 메커니즘

결제 페이지 진입 시 재고를 임시 확보하고, TTL 내 미결제 시 자동 복구:

```
예약 시작 → 재고 차감(hold) → PAYMENT_WAITING (TTL: 10분)
  ├─ 결제 완료 → CONFIRMED
  └─ 10분 초과 → 스케줄러가 ReservationCancelledEvent 발행 → 재고 자동 복구
```

- `Reservation`에 `holdExpiredAt(Instant)` 필드 추가
- 스케줄러(`@Scheduled`) 또는 지연 이벤트로 만료된 hold 자동 취소
- 멱등성 보장: `reservationRequestId`(UUID)를 클라이언트에서 발급, 중복 요청 방지

### 4-7. 동시성 처리 전략

**1차 구현: Pessimistic Lock**
- `InventoryJpaRepository`에 `@Lock(PESSIMISTIC_WRITE)` → `SELECT ... FOR UPDATE`
- 트랜잭션 범위 최소화

**향후 확장**
- Redis Distributed Lock (Redisson) — 멀티 인스턴스
- Optimistic Lock (@Version) + 재시도 — 충돌 낮은 경우

### Phase 4 테스트 (최고 중요)

- Reservation 도메인 단위 테스트 (상태 전환, 환불 계산)
- CustomerCreateReservationService 단위 테스트 (정상/재고부족)
- **동시성 테스트**: `CountDownLatch` + `ExecutorService` → 동일 재고에 N개 동시 예약 → 재고 수만큼만 성공 검증
- 예약 API REST Docs 테스트

---

## Phase 5: Supplier 연동

### 5-1. 도메인 모델

위치: `core.supplier.domain.model`

| 파일 | 설명 |
|------|------|
| `Supplier.java` | id, name, code, apiEndpoint, status |
| `SupplierAccommodation.java` | 외부 숙소 → 내부 숙소 매핑 |
| `SupplierRoomMapping.java` | 외부 객실 ID ↔ 내부 객실 ID |

### 5-2. Canonical Model & 데이터 매핑

외부 공급사마다 객실 명칭, 편의시설 코드가 상이 → 내부 표준 규격으로 변환하는 레이어 필요:

| 파일 | 설명 |
|------|------|
| `domain/model/CanonicalRoom.java` | 내부 표준 객실 모델 (공급사 데이터 → 이 형태로 정규화) |
| `domain/model/CanonicalPrice.java` | 내부 표준 가격 모델 |
| `domain/model/SupplierFieldMapping.java` | 공급사별 필드 매핑 테이블 (외부 시설코드 → 내부 Facility Enum) |
| `domain/service/SupplierDataNormalizer.java` | 공급사 응답 → Canonical Model 변환 로직 |

### 5-3. 포트 & 어댑터

| 레이어 | 파일 | 설명 |
|--------|------|------|
| port/out | `SupplierClient.java` | 공급사 API 추상화. searchRooms, checkAvailability, createBooking |
| port/out | `SupplierRepository.java` | 공급사 정보 저장/조회 |
| external | `SupplierRestAdapter.java` | RestClient 구현. `Executors.newVirtualThreadPerTaskExecutor()` 활용 |
| port/in | `SyncSupplierInventoryUseCase.java` | 외부 재고 동기화 |
| service | `SyncSupplierInventoryService.java` | SupplierDataNormalizer 통해 정규화 후 내부 Inventory/Price 저장 |

---

## Phase 6: 설계 중심 도메인 (ERD + API 스켈레톤)

> 내부 로직은 주석으로 구현 방안 제시. 메서드 시그니처와 ERD만 확정.

### 6-1. Member (회원)

`core.member` — 최소 데이터: id, name, phone, email, role(Enum), status(Enum)

### 6-2. Coupon (쿠폰)

`core.coupon` — Coupon(discountType, amount, validFrom/To), CouponAccommodationMapping(1:1/1:N), MemberCoupon(발급내역)

### 6-3. Payment (결제)

`core.payment` — Payment(reservationId, amount, method, status), PaymentGatewayClient(외부 PG 포트)

### 6-4. Review (리뷰)

`core.review` — Review(reservationId, rating(BigDecimal), content, images), 정렬/객실별 필터

### 6-5. Wishlist (찜)

`core.wishlist` — Wishlist(memberId, accommodationId)

---

## Phase 7: 횡단 관심사

### 7-1. Security
- `SecurityConfig.java` — 채널별 URL 접근 제어 (ADMIN, PARTNER, CUSTOMER)
- JWT 기반 인증 필터
- `@CurrentUser` 커스텀 어노테이션

### 7-2. 다국어 (i18n)
- `_translation` 테이블 패턴 (AccommodationTranslation, RoomTranslation)

### 7-3. 이미지 처리
- `ImageUploader` 인터페이스 + `LocalImageUploader`(개발용), 향후 S3

### 7-4. Logging / Tracing
- `MdcFilter` — trace_id를 MDC에 세팅
- `RequestLoggingInterceptor` — 요청/응답 로깅

---

## 횡단 체크포인트

> 각 Phase 구현 시 함께 점검해야 할 항목

| 체크포인트 | 관련 Phase | 상태 |
|-----------|-----------|------|
| 숙박/대실 재고 상호 배타 동기화 | Phase 2, 4 | ✅ 구현 (InventoryDomainService, 30분 블록) |
| 청소 시간(Buffer Time) — 대실 슬롯 사이 최소 정비시간 | Phase 2 | ✅ 구현 (BLOCKED 상태) |
| VAT(부가세) 포함/불포함 처리 | Phase 2 | ✅ 구현 (PriceDomainService) |
| 검색 성능 최적화 (QueryDSL + 배치 쿼리) | Phase 3 | ✅ 구현 (2단계 배치 패턴) |
| N+1 방지 — 배치 조회 | Phase 3 | ✅ 구현 (IN절 배치) |
| 재고 선점(Hold) + TTL 자동 복구 | Phase 4 | ✅ 구현 (10분 Hold + @Scheduled 자동 취소) |
| 멱등성(Idempotency) — 예약 중복 요청 방지 | Phase 4 | ✅ 구현 (reservationRequestId UUID) |
| Supplier Canonical Model 변환 | Phase 5 | ✅ 구현 (CanonicalRoom/Price + Normalizer) |
| Virtual Threads 적용 (외부 API 호출) | Phase 5 | 미적용 (현재 Mock, 실 연동 시 적용) |

---

## Phase 9: 최종 점검

### 9-1. 설계 결정 확정

| 항목 | 결정 내용 |
|------|----------|
| `AccommodationType.EXTERNAL` | 외부 공급사 단일 타입. 공급사 구분은 `supplier_accommodation_mapping` |
| `supplierManaged` 플래그 | 관리자 전용 ON/OFF. 파트너 변경 불가 |
| 관리자 가격 조정 범위 | 이 프로젝트에서 관리자 역할 = 승인 한정. 가격 조정은 설계 수준 완료 |
| `AccommodationSummary.lowestPrice` null | 재고 없음 = 해당 날짜 판매 안 함. null 표시는 정상 |

### 9-2. 설계 문서 추가

| 파일 | 내용 |
|------|------|
| `docs/design/supplier-sync-design.md` | Supplier 연동 구조, Virtual Thread 병렬 동기화 설계, 미구현 항목 목록 |
| `docs/design/search-performance-design.md` | 스냅샷 테이블, Redis 캐시, CQRS 확장 전략, 구현 우선순위 |

### 9-3. 버그 수정

| 파일 | 버그 | 수정 내용 |
|------|------|----------|
| `AdminApproveModificationService` | `latitude`/`longitude` null 체크 누락으로 기존 좌표 덮어씀 | 기존 좌표 보존 로직 추가 |
| `HoldExpirationScheduler` | HOURLY 예약 만료 시 TimeSlotInventory 복구 없음 | `restoreHourlyInventory()` 추가 |

### 9-4. 테스트 추가

| 테스트 | 내용 |
|--------|------|
| `HoldExpirationSchedulerTest` | 만료 예약 → CANCELLED + 재고 복구 확인, 미만료 예약 → 상태 유지 확인 |

### 9-5. 미완료 항목

| 항목 | 사유 |
|------|------|
| Demo UI QA (Section 5) | 서버 실행 후 직접 확인 필요 |
