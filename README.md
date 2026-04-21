# OTA Accommodation Platform

OTA(Online Travel Agency) 숙박 예약 플랫폼 백엔드. 실서비스 수준의 도메인 복잡성과 아키텍처 결정을 직접 설계하고 구현한 프로젝트입니다.

> **데모**: 서버 실행 후 [http://localhost:8080](http://localhost:8080) — 시나리오별 인터랙티브 데모 페이지 제공

---

## 무엇을 중점적으로 설계했나

### 1. 헥사고날 아키텍처 + 3채널 완전 분리

비즈니스 로직이 프레임워크와 인프라에 종속되지 않도록 Ports & Adapters 구조를 적용했습니다.  
OTA의 특성을 반영해 **3개 채널을 최상위에서 격리**했습니다.

```
┌──────────────────────────────────────────────────────────────┐
│                       Inbound Adapters                        │
│  ┌────────────┐    ┌─────────────┐    ┌─────────────┐       │
│  │   admin/   │    │  extranet/  │    │  customer/  │       │
│  │  (관리자)  │    │ (파트너센터) │    │  (고객앱)   │       │
│  └─────┬──────┘    └──────┬──────┘    └──────┬──────┘       │
└────────┼─────────────────┼─────────────────── ┼─────────────┘
         │ port/in          │ port/in             │ port/in
         ▼                  ▼                     ▼
┌──────────────────────────────────────────────────────────────┐
│                    Application Layer                          │
│   채널별 UseCase Service  (채널 간 직접 참조 금지)             │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─    │
│           core/ 공유 도메인 서비스                            │
│  (SyncSupplierInventoryService, HoldExpirationScheduler)     │
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
│  ┌─────────────────────────┐  ┌──────────────────────────┐  │
│  │  *JpaAdapter (MySQL)    │  │  SupplierAdapter         │  │
│  │  QueryDSL 동적 검색      │  │  CanonicalModel 변환     │  │
│  │  Pessimistic Lock       │  │  List<SupplierClient> 다형│  │
│  └─────────────────────────┘  └──────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘

의존성 방향: admin/extranet/customer → core (단방향)
채널 간 직접 참조 금지 / core는 채널을 참조하지 않음
```

### 2. 파트너 수정 승인 워크플로우

파트너가 숙소 정보를 수정하면 즉시 반영하지 않고, **관리자 승인 후 실제 데이터에 반영**됩니다.

```
파트너 수정 요청
  └─→ UpdateAccommodationCommand 전체를 JSON 직렬화
      └─→ accommodation_modification_request 테이블에 PENDING 저장

관리자 승인
  └─→ JSON 역직렬화 → null-safe partial update
      └─→ null 필드는 기존 값 유지, 변경 필드만 반영
```

수정 항목이 늘어도 DB 컬럼 추가 없이 JSON 컬럼 하나로 확장됩니다.

### 3. 예약 동시성 3중 방어

```
① 멱등성 키 (reservationRequestId)
   → 클라이언트 재시도로 인한 중복 예약 차단

② DB Pessimistic Lock (날짜별 row-level)
   → @Lock(PESSIMISTIC_WRITE) + 날짜 순서 lock 획득
   → 동시 요청 중 재고 수량만큼만 성공

③ Hold 만료 스케줄러 (HoldExpirationScheduler)
   → 결제 미완료 예약 10분 후 자동 취소 + 재고 복구
```

`ConcurrencyReservationTest` — `CountDownLatch + ExecutorService`로 10개 동시 요청 → 재고 3개만 성공 검증.

### 4. 숙박 / 대실 이중 예약 모델

| 구분 | 재고 단위 | 예약 흐름 |
|------|-----------|-----------|
| 숙박 | `Inventory` — 날짜별 객실 수 | 체크인~체크아웃 날짜 범위 차감 |
| 대실 | `TimeSlotInventory` — 30분 블록 | 시간 슬롯 점유 |

파트너는 날짜별 재고 설정과 시간 슬롯 오픈을 extranet에서 별도로 관리합니다.

### 5. 외부 공급사 연동의 확장성

신규 공급사 추가 시 `SupplierClient` 구현체 하나만 추가하면 됩니다.

```java
// 공급사 클라이언트를 List로 주입받아 supplierCode로 런타임 선택
List<SupplierClient> supplierClients;
SupplierClient client = supplierClients.stream()
    .filter(c -> c.getSupplierCode().equals(supplierCode))
    .findFirst();
```

외부 공급사의 카테고리 체계는 `SupplierCategoryMappingJpaEntity`로 플랫폼 태그 시스템과 연결하고, 공급사 전용 태그 그룹(`TagGroup.supplierId`)으로 자체 태그와 완전히 격리합니다.

### 6. 검색 성능 — 2단계 배치 조회

```
1단계: QueryDSL BooleanBuilder로 숙소 ID 목록 조회 (가격 필터 = WHERE EXISTS)
2단계: 대표이미지 IN 배치 / 최저가 GROUP BY 배치 → Java Map으로 조립

서브쿼리 제거, Java 후처리 금지 → 페이징 정확성 보장
```

---

## Tech Stack

| 구분 | 기술 |
|------|------|
| Language | Java 21 (Record, Pattern Matching, Virtual Threads) |
| Framework | Spring Boot 4.0.5 |
| DB | MySQL + QueryDSL |
| Test | JUnit 5 · Mockito · Testcontainers · Spring REST Docs |
| Build | Gradle |

---

## Package Structure

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

## Domain Overview

| 도메인 | 핵심 구현 |
|--------|-----------|
| Accommodation | 등록·수정·승인 워크플로우, 다국어 번역 구조 |
| Room / RoomOption | 숙박·대실 객실, 이미지, 취소 정책 |
| Inventory | 날짜별 재고 (숙박) / 30분 시간 슬롯 (대실) |
| Price | 날짜별 요금, 숙박·대실·주말 가격 분리 |
| Reservation | 예약 상태 머신, 동시성 제어, Hold 만료 |
| Tag | 관리자 정의 태그 + 공급사 전용 태그 그룹 |
| Supplier | 외부 공급사 연동, CanonicalModel, 카테고리 매핑 |

---

## Build & Run

```bash
./gradlew build        # 빌드 + 테스트
./gradlew bootRun      # 실행 (MySQL 필요)
./gradlew test         # 테스트만 (Testcontainers 자동 DB)
```

---

## Demo Pages

서버 실행 후 브라우저에서 접근 가능한 인터랙티브 데모 페이지입니다.

| 페이지 | 설명 |
|--------|------|
| [http://localhost:8080/index.html](http://localhost:8080/index.html) | 플랫폼 소개 & 아키텍처 |
| [http://localhost:8080/demo-partner.html](http://localhost:8080/demo-partner.html) | 파트너 시나리오 |
| [http://localhost:8080/demo-customer.html](http://localhost:8080/demo-customer.html) | 고객 시나리오 |
| [http://localhost:8080/demo-admin.html](http://localhost:8080/demo-admin.html) | 관리자 시나리오 |
| [http://localhost:8080/demo-supplier.html](http://localhost:8080/demo-supplier.html) | 공급사 연동 시나리오 |

---

## Documentation

| 문서 | 설명 |
|------|------|
| [다국어 설계](docs/design/multilingual.md) | Translation 테이블 구조, 언어 코드 규칙, 폴백 정책, 확장 방법 |
| [공급사 카테고리 매핑 샘플](docs/design/supplier-category-sample.sql) | MinhyukHouse 공급사 카테고리 → 플랫폼 태그 매핑 예시 데이터 |
| [코드 리뷰 체크리스트](docs/plan/code-review-checklist.md) | 헥사고날 아키텍처 준수 여부, 네이밍, 동시성 등 검토 항목 |
