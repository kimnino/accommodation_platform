# OTA Accommodation Platform

OTA(Online Travel Agency) 숙박 예약 플랫폼 백엔드. 실서비스 수준의 도메인 복잡성과 아키텍처 결정을 직접 설계하고 구현한 프로젝트입니다.

> **데모**: 서버 실행 후 `http://localhost:8080/api-test.html` — 역할별 토큰 발급 + API 호출 + 시나리오 자동 실행

---

## 목차

1. [빠른 시작](#빠른-시작)
2. [무엇을 만들었나](#무엇을-만들었나)
3. [아키텍처](#아키텍처)
4. [구현 도메인](#구현-도메인)
5. [설계 도메인 (미구현)](#설계-도메인-미구현)
6. [핵심 기술 결정](#핵심-기술-결정)
7. [테스트 전략](#테스트-전략)
8. [Tech Stack](#tech-stack)
9. [설계 문서](#설계-문서)

---

## 빠른 시작

**사전 조건**: Java 21, Docker

```bash
# MySQL 컨테이너 실행 (포트 3307)
docker run -d --name ota-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=accommodation \
  -p 3307:3306 mysql:8.0.36

# 서버 실행 (ddl-auto: create — DB 스키마 자동 생성 + data.sql 샘플 데이터 로드)
./gradlew bootRun

# 테스트 (Testcontainers — 별도 MySQL 불필요)
./gradlew test
```

서버 실행 후 `http://localhost:8080/api-test.html` 접속:
1. 좌측 패널에서 역할(Admin/Extranet/Customer) 선택 후 **토큰 발급**
2. **시나리오 탭**에서 원하는 시나리오 **[실행]** 버튼 클릭 → 단계별 결과 자동 확인

---

## 무엇을 만들었나

OTA 플랫폼의 핵심 백엔드를 설계하고 구현했습니다. 단순한 CRUD를 넘어 **실서비스에서 만나는 복잡성**을 직접 다뤘습니다.

| 구분 | 내용 |
|------|------|
| 채널 분리 | 관리자 / 파트너(Extranet) / 고객 3채널을 아키텍처 레벨에서 격리 |
| 이중 예약 모델 | 숙박(날짜별 재고) + 대실(30분 시간 슬롯) 동일 객실에서 동시 관리 |
| 동시성 제어 | Pessimistic Lock + 멱등성 키 + Hold 만료 스케줄러 3중 방어 |
| 파트너 워크플로우 | 수정 요청 → 관리자 승인 → 반영 구조 |
| 공급사 연동 | 외부 OTA 공급사 어댑터 구조, 카테고리 매핑 |
| 설계 문서화 | 미구현 도메인(결제, 리뷰, 쿠폰)도 도메인 모델 + 이벤트 설계까지 완성 |

---

## 아키텍처

### 헥사고날 아키텍처 + 3채널 분리

비즈니스 로직이 프레임워크·인프라에 종속되지 않도록 Ports & Adapters 구조를 적용했습니다.

```
┌──────────────────────────────────────────────────────────────┐
│                       Inbound Adapters                        │
│  ┌────────────┐    ┌─────────────┐    ┌─────────────┐       │
│  │   admin/   │    │  extranet/  │    │  customer/  │       │
│  │  (관리자)  │    │ (파트너센터) │    │  (고객앱)   │       │
│  └─────┬──────┘    └──────┬──────┘    └──────┬──────┘       │
└────────┼─────────────────┼──────────────────┼──────────────┘
         │ port/in          │ port/in           │ port/in
         ▼                  ▼                   ▼
┌──────────────────────────────────────────────────────────────┐
│                    Application Layer                          │
│   채널별 UseCase Service  (채널 간 직접 참조 금지)             │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─    │
│           core/ 공유 도메인 서비스                            │
└────────────────────────┬─────────────────────────────────────┘
                         │ port/out
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                      Domain Layer                             │
│   Accommodation · Reservation · Inventory · Room · Tag       │
│   순수 Java — 프레임워크 의존성 제로                          │
│   도메인 서비스는 @Bean 등록 (DomainServiceConfig)            │
└──────────────────────────────────────────────────────────────┘
                         ▲ port/out 구현
┌──────────────────────────────────────────────────────────────┐
│                     Outbound Adapters                         │
│  *JpaAdapter (MySQL + QueryDSL)  |  SupplierAdapter          │
└──────────────────────────────────────────────────────────────┘
```

**의존성 방향**: `admin/extranet/customer → core` (단방향, 역방향 불가)

### 패키지 구조

```
com.accommodation.platform/
  core/{domain}/          # 공유 도메인 & 인프라 (채널 독립)
    domain/model/         # 순수 Java 엔티티 (프레임워크 의존 없음)
    application/port/in/  # Inbound Port (UseCase 인터페이스)
    application/port/out/ # Outbound Port (Repository 인터페이스)
    adapter/out/          # JPA Adapter, External API Adapter

  admin/{domain}/         # 관리자 채널 (숙소 승인, 수정 승인, 태그 관리)
  extranet/{domain}/      # 파트너 채널 (숙소·객실 등록, 재고·가격 설정)
  customer/{domain}/      # 고객 채널 (숙소 검색, 예약/취소)
  common/                 # 공통 설정, 예외 처리, ApiResponse 포맷
```

---

## 구현 도메인

### Accommodation (숙소)

- 파트너가 숙소·객실·옵션을 등록, 관리자가 승인
- 수정 요청은 즉시 반영하지 않고 **관리자 승인 후 반영** (ModificationRequest)
- 다국어 번역: `AccommodationTranslationJpaEntity` — ko/en/ja 별도 테이블, Accept-Language 헤더 기반 자동 적용
- 이미지는 상대경로만 저장 (base URL 교체만으로 CDN 전환 가능)

### Room / RoomOption

- 객실(Room) → 옵션(RoomOption) 1:N 구조로 상품 분기
- 옵션별 취소 정책(FREE_CANCELLATION / NON_REFUNDABLE / PARTIAL_REFUND)
- 대실 가능 시간: 옵션의 `hourlyStartTime / hourlyEndTime`
- 숙박 체크인/아웃 시간: 옵션별 재정의 가능 (레이트 체크아웃 등), 미설정 시 숙소 기본값 fallback

```
숙소 기본 체크아웃: 11:00
  └─ 일반 옵션       → check_out_time: null  → 응답: 11:00 (숙소 기본값)
  └─ 레이트체크아웃  → check_out_time: 13:00 → 응답: 13:00 (옵션 재정의)
```

### Inventory / Price

- **숙박 재고**: `Inventory` — 날짜별 `remaining_quantity`
- **대실 재고**: `TimeSlotInventory` — 30분 단위 슬롯 (`AVAILABLE / OCCUPIED / BLOCKED`)
- 요금: `RoomPrice` — 날짜별 STAY/HOURLY 가격, 주말 가격 분리
- VAT 계산은 `PriceDomainService` (순수 Java 도메인 서비스)

### Reservation (예약)

예약 상태 머신:

```
PAYMENT_WAITING → CONFIRMED → CHECKED_IN → CHECKED_OUT
      └──────────────────────→ CANCELLED (고객 취소 / Hold 만료)
```

- 숙박 예약: 날짜 범위의 `Inventory.remaining_quantity` 차감
- 대실 예약: 시간 슬롯 `OCCUPIED` 처리 + 버퍼 슬롯 `BLOCKED`
- Hold 만료 스케줄러: 10분 내 결제 미완료 시 자동 취소 + 재고 복구

### Tag / Region

- 태그 그룹(TagGroup) + 태그(Tag) 2단계 구조
- 공급사 전용 태그 그룹(`supplierCode`) 으로 플랫폼 태그와 격리
- 지역(Region)은 숙소와 N:1 매핑, 지역 기반 검색 지원

### Supplier (외부 공급사 연동)

```java
// 공급사 클라이언트를 List로 주입받아 supplierCode로 런타임 선택
List<SupplierClient> supplierClients;
SupplierClient client = supplierClients.stream()
    .filter(c -> c.getSupplierCode().equals(supplierCode))
    .findFirst();
```

- `SupplierCategoryMappingJpaEntity` — 외부 카테고리 ↔ 플랫폼 태그 매핑
- `CanonicalModel` — 공급사별 상이한 데이터 구조를 플랫폼 표준으로 변환
- 신규 공급사 추가 = `SupplierClient` 구현체 1개 추가

### Security

- JWT 인증 (HMAC-SHA512, 24시간 만료)
- 채널별 Role 기반 접근 제어: `ADMIN / PARTNER / CUSTOMER`
- `JwtAuthenticationFilter` — Bearer 토큰 파싱 → X-Partner-Id / X-Member-Id 헤더 자동 주입

---

## 설계 도메인 (미구현)

코드는 도메인 모델 + JPA 엔티티 + Port 스켈레톤까지 구현되어 있으며, 유스케이스 구현체는 제외합니다.

### Member (회원)

예약에 필요한 최소한의 식별자(이름, 연락처, 이메일)만 보유합니다. 실제 서비스라면 OAuth2 소셜 로그인, 이메일 인증, 비밀번호 정책 등이 추가됩니다.

```
member
  ├─ id, email, name, phone
  ├─ role (CUSTOMER / PARTNER / ADMIN)
  └─ status (ACTIVE / SUSPENDED / WITHDRAWN)
```

### Payment (결제)

결제는 예약 확정(`CONFIRMED`)의 선행 조건입니다. 현재 `confirm-payment` API는 결제 게이트웨이 연동 없이 즉시 확정하는 Mock 구조입니다.

**실제 설계:**

```
결제 흐름
  고객 결제 요청
    └─ PaymentGatewayClient.initiate() — PG사 결제 위젯 URL 반환
    └─ 고객 PG 결제 완료 (Redirect / Webhook)
    └─ PaymentGatewayClient.confirm() — 결제 금액/상태 검증
    └─ ReservationConfirmedEvent 발행 → 예약 CONFIRMED 전환

환불 흐름
  ReservationCancelledEvent 구독
    └─ 취소 정책 적용 (FREE_CANCELLATION / NON_REFUNDABLE / PARTIAL_REFUND)
    └─ PaymentGatewayClient.refund() — 부분/전액 환불
```

```
payment
  ├─ reservation_id, member_id
  ├─ amount (BigDecimal), method (CARD / KAKAO_PAY / NAVER_PAY)
  ├─ pg_transaction_id — PG사 거래 ID
  ├─ status (PENDING / COMPLETED / REFUNDED / FAILED)
  └─ paid_at, refunded_at
```

### Review (리뷰)

체크아웃 완료 후 작성 가능한 구조입니다. `ReservationConfirmedEvent` 구독 후 체크아웃 시점에 작성 가능 상태로 전환합니다.

**설계 포인트:**
- 리뷰는 예약 1건당 1회만 작성 가능 (`reservation_id` unique)
- 별점: 전체 평점 + 세부 항목 (청결도, 직원 친절도, 위치, 가성비)
- 숙소 평균 평점은 리뷰 저장 시 `accommodation` 테이블에 denormalize (매번 AVG 집계 방지)
- 이미지 첨부 가능 (`ReviewImage` — 최대 5장)
- 파트너 답변 기능 (`ReviewReply`)

```
review
  ├─ reservation_id (unique), accommodation_id, member_id
  ├─ overall_rating (1~5), cleanliness_rating, staff_rating, location_rating
  ├─ content (최대 1000자)
  ├─ status (ACTIVE / HIDDEN / DELETED)
  └─ images: List<ReviewImage>
```

### Coupon (쿠폰)

```
coupon
  ├─ code (unique), discount_type (PERCENTAGE / FIXED_AMOUNT)
  ├─ discount_amount, minimum_order_amount, maximum_discount_amount
  ├─ valid_from, valid_to, usage_limit, used_count
  └─ 숙소별 적용 제한: CouponAccommodationMapping

member_coupon
  ├─ member_id, coupon_id
  └─ status (AVAILABLE / USED / EXPIRED)
```

**쿠폰 적용 흐름:**
1. 예약 생성 시 쿠폰 코드 입력 → 유효성 검증 (기간, 잔여 수량, 최소 주문금액)
2. 결제 금액에서 할인 적용 (최대 할인액 캡 적용)
3. 결제 완료 후 `used_count` 증가 + `member_coupon.status = USED`
4. 예약 취소 시 쿠폰 복원 (`status = AVAILABLE`)

### Wishlist (찜)

```
wishlist
  ├─ member_id, accommodation_id
  └─ created_at
```

---

## 핵심 기술 결정

### 1. 예약 동시성 — 3중 방어

```
① 멱등성 키 (reservation_request_id: UUID)
   → 클라이언트 재시도로 인한 중복 예약 차단

② DB Pessimistic Lock (날짜별 row-level)
   → @Lock(PESSIMISTIC_WRITE) + 날짜 오름차순 lock 획득 (데드락 방지)
   → 동시 요청 중 재고 수량만큼만 성공

③ Hold 만료 스케줄러 (HoldExpirationScheduler)
   → 결제 미완료 예약 10분 후 자동 취소 + 재고 복구
```

**확장 시 고려:** 이중화 서버 환경에서는 Pessimistic Lock 대신 Redis Distributed Lock 적용. 동시에 캐시 레이어를 붙이면 Redis 활용도가 함께 올라감.

검증: `ConcurrentReservationScenarioTest` — CountDownLatch + ExecutorService 10개 동시 요청, 재고 1개만 성공 확인

### 2. 숙박/대실 공유 재고

동일 객실에 숙박과 대실이 동시에 운영될 때 재고 충돌을 처리합니다.

| 재고 | 테이블 | 단위 |
|------|--------|------|
| 숙박 | `inventory` | 날짜별 `remaining_quantity` |
| 대실 | `time_slot_inventory` | 30분 슬롯 상태 |

**충돌 방지 규칙:**
- `remaining_quantity = 0`이 된 시점 → 해당 기간의 대실 슬롯 `BLOCKED`
- `remaining_quantity > 0`이면 숙박·대실 동시 판매 가능 (방이 2개 이상인 경우)
- 숙박 예약 취소 후 `remaining_quantity > 0` 복구 시 → 슬롯 `AVAILABLE` 복원

예시 (방 1개):
```
숙박 예약 (4/25 체크인 15:00, 4/26 체크아웃 11:00) → remaining = 0
  → 4/25 15:00 이후 슬롯 BLOCKED
  → 4/26 11:00 이전 슬롯 BLOCKED
  → 그 외 슬롯(4/25 10:00~14:30)은 AVAILABLE 유지
```

### 3. 파트너 수정 승인 워크플로우

```
파트너 수정 요청
  └─ UpdateAccommodationCommand → JSON 직렬화
     └─ accommodation_modification_request 테이블에 PENDING 저장

관리자 승인
  └─ JSON 역직렬화 → null-safe partial update
     └─ null 필드는 기존 값 유지, 변경 필드만 반영
```

수정 항목이 늘어도 DB 컬럼 추가 없이 JSON 컬럼 하나로 확장됩니다.

향후 확장: 수정 요청 발생 시 Slack/자체 알림 서비스로 관리자에게 실시간 알림 (이벤트 기반으로 발행자 코드 변경 없이 구독자 추가 가능)

### 4. 이벤트 기반 아키텍처 (설계)

현재는 동기 직접 호출. 도메인 이벤트 POJO는 정의되어 있으며, 발행/구독 인프라 연결이 다음 단계입니다.

**정의된 이벤트:**

| 이벤트 | 발행 시점 | 주요 구독자 |
|--------|-----------|-------------|
| `ReservationCreatedEvent` | 예약 생성 | InventoryService(홀드), NotificationService(접수 알림) |
| `ReservationConfirmedEvent` | 결제 확정 | PaymentService, NotificationService, ReviewService |
| `ReservationCancelledEvent` | 예약 취소/만료 | InventoryService(복구), PaymentService(환불), NotificationService |
| `InventoryDepletedEvent` | 재고 0 도달 | SearchSnapshotService(품절 반영), NotificationService(파트너 알림) |
| `InventoryRestoredEvent` | 재고 복구 | SearchSnapshotService(재판매 가능) |
| `AccommodationApprovedEvent` | 관리자 승인 | SearchIndexService, NotificationService |
| `PriceChangedEvent` | 가격 변경 | SearchSnapshotService(최저가 갱신), CacheService(캐시 무효화) |

**전환 전략:** Spring ApplicationEvent(1단계) → Kafka/RabbitMQ(2단계). Outbound Port(`EventPublishPort`) 추가로 발행자 코드 변경 없이 브로커 교체 가능.

### 5. 검색 성능 — 2단계 배치 조회

```
1단계: QueryDSL BooleanBuilder → accommodation_id 목록 + 페이징
       (가격 필터 = WHERE EXISTS 절 처리, Java 후처리 금지 → 페이징 정확성 보장)

2단계: IN(id_list)로 대표이미지, 최저가 배치 조회 → Java Map으로 조립
       (N+1 완전 제거)
```

**다음 단계 (설계):**
```
accommodation_search_snapshot 테이블 도입
  → lowest_stay_price, is_available, available_from/until 비정규화
  → 이벤트 기반 갱신 (PriceChangedEvent, InventoryDepletedEvent)
  → 검색 쿼리에서 inventory/room_price 조인 제거
```

### 6. Redis 캐시 전략 (설계)

| 캐시 키 | 대상 | TTL | 무효화 트리거 |
|---------|------|-----|---------------|
| `acc:card:{id}:{locale}` | 숙소 카드 (정적 정보) | 30분 | 수정 승인 완료 |
| `tags::{type}:{accType}` | 태그 목록 | 1시간 | 관리자 태그 수정 |
| `regions::{accType}` | 지역 목록 | 1시간 | 관리자 지역 수정 |

캐시하지 않는 것: `lowestPrice`, `hasAvailableRoom` — 실시간 반영 필수

무효화는 `PriceChangedEvent` / `AccommodationApprovedEvent` 구독 후 `CacheService.evict()` 호출. 메시지 브로커 도입 시 캐시 갱신도 이벤트 기반으로 전환.

### 7. 다국어 지원

- 번역 가능 엔티티: `AccommodationTranslation`, `RoomTranslation`, `RoomOptionTranslation`
- Accept-Language 헤더 → `LocaleContextHolder.getLocale()` 자동 감지
- ko: 번역 DB 조회 스킵, 원본 사용
- en/ja: 배치 로드 후 Java Map 조립, 번역 없으면 원본 fallback

---

## 테스트 전략

### 단위 테스트 (`@WebMvcTest` + Mockito)

Controller 레이어 + Spring REST Docs 문서 자동 생성:

```
build/generated-snippets/
  customer/accommodation/search/
  customer/accommodation/detail/
  customer/reservation/stay/
  ...
```

### 도메인 단위 테스트

순수 Java 도메인 모델 검증:
- `RoomOptionTest` — 생성 검증, updateInfo 동작
- `ReservationTest` — 상태 전환, 홀드 만료 검증
- `PriceDomainServiceTest` — VAT 계산, 날짜별 요금 합산

### 통합 테스트 (`@SpringBootTest` + Testcontainers)

실제 MySQL 컨테이너에서 전체 플로우 검증:

| 테스트 클래스 | 시나리오 |
|---------------|---------|
| `CustomerStayReservationScenarioTest` | 숙소 검색 → 상세 → 예약 → 결제 확정 → 재고 감소 확인 |
| `CustomerHourlyReservationScenarioTest` | 대실 예약 → 결제 → 슬롯 OCCUPIED 확인 |
| `InventoryDepletionScenarioTest` | 재고 1개 → 첫 예약 성공 → 두 번째 예약 409 차단 |
| `ConcurrentReservationScenarioTest` | 10개 동시 요청 → 재고 1개만 성공 (Pessimistic Lock 검증) |

### 인터랙티브 시나리오 테스트 (`api-test.html`)

브라우저에서 클릭 한 번으로 전체 플로우를 순서대로 실행:

| 시나리오 | 내용 |
|----------|------|
| S-A1 관리자: 숙소 승인 | 대기 목록 조회 → 승인 |
| S-P1 파트너: 전체 설정 | 숙소 등록 → 객실 → 옵션 → 지역 → 재고 → 가격 |
| S-C1 고객: 숙박 예약 | 검색 → 상세 → 예약 → 결제 확정 |
| S-C2 고객: 대실 예약 | 상세 → 대실 예약 → 결제 확정 |
| S-C3 고객: 재고 소진 차단 | 예약 성공 → 동일 옵션 재예약 시도 → 409 차단 확인 |
| S-C4 고객: 예약 취소 | 예약 → 취소 |

각 스텝 결과에 기대 상태코드 표시 (✅ / ❌), 실패 시 이후 스텝 SKIPPED.

---

## Tech Stack

| 구분 | 기술 |
|------|------|
| Language | Java 21 (Record, Pattern Matching) |
| Framework | Spring Boot 4.0.5 |
| DB | MySQL 8 + QueryDSL |
| Persistence | Spring Data JPA + MapStruct |
| Security | Spring Security + JWT (HMAC-SHA512) |
| Test | JUnit 5 · Mockito · Testcontainers · Spring REST Docs |
| Build | Gradle |

---

## 설계 문서

| 문서 | 내용 |
|------|------|
| [이벤트 기반 아키텍처](docs/design/event-architecture.md) | 8개 이벤트 발행/구독 설계, Spring ApplicationEvent → Kafka 전환 전략 |
| [Redis 캐시 설계](docs/design/redis-cache-design.md) | 숙소 카드 캐시, 배치 조회, 무효화 전략 |
| [검색 성능 설계](docs/design/search-performance-design.md) | 2단계 배치 조회, 스냅샷 테이블 도입 계획 |
| [다국어 설계](docs/design/multilingual.md) | Translation 테이블 구조, 언어 코드 규칙, 폴백 정책 |
| [공급사 연동 설계](docs/design/supplier-sync-design.md) | CanonicalModel, 카테고리 매핑, 동기화 전략 |
| [코드 컨벤션](docs/conventions/code-conventions.md) | 네이밍, 레이어 규칙, 동시성, 테스트 가이드 |
