# OTA Accommodation Platform

OTA 숙박 플랫폼 백엔드 시스템. 핵심 도메인(숙소, 예약, 재고)을 설계하고 구현하는 학습 프로젝트.

## Tech Stack

- Java 21 / Spring Boot 4.0.5 / Gradle
- MySQL (Main DB)
- Lombok
- 테스트: JUnit 5, Mockito, Testcontainers
- API 문서: Spring REST Docs (테스트 기반 문서 자동 생성)

## Architecture

**헥사고날 아키텍처 (Ports & Adapters)** + **채널별 최상위 분리**.

### 패키지 구조

```
com.accommodation.platform/
  core/{domain}/                           # 공유 도메인 & 인프라
    domain/
      model/                               # 엔티티, VO, Enum
      service/                             # 도메인 서비스 (순수 비즈니스 규칙)
      event/                               # 도메인 이벤트
    application/
      port/in/                             # Inbound Port (유스케이스 인터페이스)
      port/out/                            # Outbound Port (영속성/외부 시스템 인터페이스)
      service/                             # 유스케이스 구현체
    adapter/
      out/persistence/                     # JPA Repository, Entity 매핑
      out/external/                        # 외부 API 클라이언트

  admin/{domain}/                          # 관리자 채널
    application/
      port/in/                             # 관리자 전용 Inbound Port
      service/                             # 관리자 전용 유스케이스 구현체
    adapter/in/web/                        # 관리자 REST API (Controller, Request/Response DTO)

  extranet/{domain}/                       # 숙소 파트너 채널
    application/
      port/in/                             # 파트너 전용 Inbound Port
      service/                             # 파트너 전용 유스케이스 구현체
    adapter/in/web/                        # 파트너 REST API

  customer/{domain}/                       # 고객 채널
    application/
      port/in/                             # 고객 전용 Inbound Port
      service/                             # 고객 전용 유스케이스 구현체
    adapter/in/web/                        # 고객 REST API
```

### 레이어 규칙

**domain (최내부)** — 프레임워크 의존성 제로
- 엔티티, VO는 순수 Java. JPA/Spring 어노테이션 금지
- 비즈니스 불변식(invariant)은 엔티티 메서드로 보호
- 도메인 이벤트는 POJO로 정의

**application (중간)** — 포트를 통해서만 외부와 소통
- Inbound Port: 유스케이스 인터페이스 (`~UseCase`, `~Query`)
- Outbound Port: 영속성/외부 시스템 인터페이스 (`~Repository`, `~Client`)
- 유스케이스 구현체는 Inbound Port를 구현하고, Outbound Port를 주입받음
- 트랜잭션 경계는 유스케이스 구현체(`@Transactional`)에서 관리

**adapter (최외부)** — 프레임워크/인프라 의존 허용
- `adapter/in/web`: Controller, Request/Response DTO. Inbound Port를 호출
- `adapter/out/persistence`: JPA Entity(`~JpaEntity`), Spring Data Repository, Outbound Port 구현체
- JPA Entity <-> 도메인 모델 변환은 `adapter/out/persistence` 내 매퍼에서 처리

### 의존성 방향

```
adapter/in  -->  application(port/in)  -->  domain
                 application(port/out) <--  adapter/out
```
- 안쪽 레이어는 바깥을 모른다. domain은 application을, application은 adapter를 참조하지 않는다
- 채널(admin/extranet/customer)은 `core`를 참조할 수 있지만, `core`는 채널을 참조하지 않는다
- 채널 간 직접 참조 금지 (admin -> extranet 불가)

### 네이밍 컨벤션

| 구분 | 패턴 | 예시 |
|------|------|------|
| Inbound Port (명령) | `{Channel}{Action}{Domain}UseCase` | `ExtranetRegisterRoomUseCase`, `AdminAdjustPriceUseCase` |
| Inbound Port (조회) | `{Channel}{Action}{Domain}Query` | `CustomerSearchAccommodationQuery`, `ExtranetGetSalesStatisticsQuery` |
| Inbound Port 구현체 | `{Channel}{Action}{Domain}Service` | `ExtranetRegisterRoomService`, `AdminAdjustPriceService` |
| Outbound Port | `{Domain}Repository`, `{Domain}Client` | `AccommodationRepository`, `SupplierClient` |
| Outbound Port 구현체 | `{Domain}{Tech}Adapter` | `AccommodationJpaAdapter`, `SupplierRestAdapter` |
| Controller | `{Channel}{Domain}Controller` | `AdminAccommodationController`, `CustomerReservationController` |
| 도메인 이벤트 | `{Domain}{PastTense}Event` | `ReservationConfirmedEvent`, `InventoryDepletedEvent` |
| JPA Entity | `{Domain}JpaEntity` | `AccommodationJpaEntity`, `RoomJpaEntity` |
| Request/Response DTO | `{Action}{Domain}Request/Response` | `RegisterRoomRequest`, `SearchAccommodationResponse` |
| Command/Query DTO | `{Action}{Domain}Command/Query` | `RegisterRoomCommand`, `SearchAccommodationCriteria` |

### 배치 기준

| 상황 | 위치 |
|------|------|
| 여러 채널에서 공유하는 로직 | `core` |
| 한 채널에서만 쓰는 유스케이스 | 해당 채널 |
| 도메인 엔티티, VO, 도메인 서비스 | 항상 `core` |
| Outbound Port + 구현체 (Repository 등) | 항상 `core` |
| Controller, Request/Response DTO | 항상 해당 채널 |
| 처음엔 한 채널 -> 나중에 공유 필요 | 공유 시점에 `core`로 이동 |

## Domains

### 구현 핵심 도메인
| 도메인 | 설명 |
|--------|------|
| Extranet (파트너 센터) | 숙소/객실 등록, 요금 및 재고(Inventory) 설정 |
| 고객 서비스 (Web/App) | 숙소 검색, 요금 조회/비교, 예약/취소 |
| Admin (관리자) | 숙소, 예약, 파트너 모니터링 및 운영 |
| Supplier 연동 | 외부 공급사 상품 통합 어댑터 |

### 설계 중심 도메인 (ERD/아키텍처만)
- 쿠폰, 리뷰, 결제, 회원 (회원은 예약에 필요한 최소 데이터만 구현)

### 구현 우선순위
숙소 등록 -> 재고 설정 -> 검색 -> 예약(동시성 처리)

## Coding Standards & Key Requirements

@docs/conventions/code-conventions.md

## 피드백 기반 개발 프로세스

각 Phase 구현 완료 후, 사용자가 피드백 파일을 작성하면 Claude가 읽고 반영하는 방식으로 진행한다.

### 피드백 파일 위치 및 네이밍

```
docs/plan/feedback/
  phase_1.md       # Phase 1 첫 번째 피드백
  phase_1_2.md     # Phase 1 두 번째 피드백
  phase_1_3.md     # Phase 1 세 번째 피드백
  phase_2.md       # Phase 2 첫 번째 피드백
  ...
```

### 워크플로우

1. Claude가 Phase N 구현 완료
2. 사용자가 `docs/plan/feedback/phase_N.md`에 피드백 작성
3. 사용자가 피드백 반영 요청 → Claude가 파일 읽고 코드에 반영
4. 추가 피드백이 있으면 `phase_N_2.md`, `phase_N_3.md` ... 순서로 반복

## Build & Run

```bash
./gradlew build        # 빌드
./gradlew test         # 테스트
./gradlew bootRun      # 실행
```
