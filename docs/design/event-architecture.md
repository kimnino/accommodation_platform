# 이벤트 기반 아키텍처 설계

## 1. 개요

### 왜 이벤트인가

현재 플랫폼은 도메인 서비스 간 동기식 직접 호출 방식으로 동작한다. 예약이 생성되면 재고 차감, 알림 발송, 결제 처리 등을 하나의 트랜잭션 안에서 순차적으로 호출한다. 이 방식은 다음과 같은 문제를 야기한다.

| 문제 | 설명 |
|------|------|
| 강한 결합 | 예약 서비스가 재고, 알림, 결제 서비스를 직접 의존 |
| 확장 어려움 | 새 후속 처리(리뷰, 쿠폰 등)를 추가할 때마다 예약 서비스를 수정해야 함 |
| 장애 전파 | 알림 발송 실패가 예약 트랜잭션 전체를 롤백시킬 수 있음 |

이벤트 기반 아키텍처를 도입하면 **발행자는 이벤트만 발행하고, 구독자가 독립적으로 반응**한다. 도메인 간 결합도가 최소화되고, 후속 처리 추가/제거가 발행자 코드 변경 없이 가능해진다.

### 현재 상태

이미 도메인 이벤트 POJO가 정의되어 있다: `ReservationCreatedEvent`, `ReservationConfirmedEvent`, `ReservationCancelledEvent`, `InventoryDepletedEvent`, `InventoryRestoredEvent`. 아직 발행/구독 인프라는 연결되지 않았다.

---

## 2. 이벤트 목록 및 발행/구독 설계

### 2.1 ReservationCreatedEvent

- **발행 시점**: 고객이 예약을 생성하고 PENDING 상태로 저장된 직후
- **발행자**: `CustomerCreateReservationService`
- **구독자 및 처리 내용**:
  - `InventoryService` -- 해당 날짜/객실의 재고 차감 확인 및 홀드 처리
  - `NotificationService` -- 고객에게 예약 접수 알림 발송, 파트너에게 신규 예약 알림

### 2.2 ReservationConfirmedEvent

- **발행 시점**: 결제가 완료되어 예약이 CONFIRMED 상태로 전환된 직후
- **발행자**: `CustomerConfirmPaymentService`
- **구독자 및 처리 내용**:
  - `PaymentService` -- 결제 확정 기록 저장
  - `NotificationService` -- 고객에게 예약 확정 알림, 파트너에게 확정 알림
  - `ReviewService` -- 체크아웃 후 리뷰 작성 가능 예약으로 등록

### 2.3 ReservationCancelledEvent

- **발행 시점**: 고객이 예약을 취소하거나, 홀드 만료 스케줄러가 미결제 예약을 자동 취소할 때
- **발행자**: `CustomerCancelReservationService` / `HoldExpirationScheduler`
- **구독자 및 처리 내용**:
  - `InventoryService` -- 차감된 재고 복구
  - `PaymentService` -- 결제 건이 있으면 환불 처리 (취소 수수료 정책 적용)
  - `NotificationService` -- 고객에게 취소 알림, 파트너에게 취소 알림

### 2.4 InventoryDepletedEvent

- **발행 시점**: 특정 날짜/객실 옵션의 재고가 0이 된 시점
- **발행자**: `InventoryService`
- **구독자 및 처리 내용**:
  - `SearchSnapshotService` -- 검색 스냅샷에 sold out 표시 반영
  - `NotificationService` -- 파트너에게 해당 객실/날짜 품절 알림

### 2.5 InventoryRestoredEvent

- **발행 시점**: 취소 등으로 sold out 상태의 재고가 다시 1 이상이 된 시점
- **발행자**: `InventoryService`
- **구독자 및 처리 내용**:
  - `SearchSnapshotService` -- 검색 스냅샷에서 sold out 해제, 재판매 가능 상태로 갱신

### 2.6 AccommodationApprovedEvent

- **발행 시점**: 관리자가 파트너의 숙소 등록/수정 요청을 승인한 직후
- **발행자**: `AdminApproveService`
- **구독자 및 처리 내용**:
  - `SearchIndexService` -- 검색 인덱스에 숙소 추가 또는 정보 갱신
  - `NotificationService` -- 파트너에게 승인 완료 알림

### 2.7 PriceChangedEvent

- **발행 시점**: 파트너가 요금을 설정하거나, 관리자가 요금을 조정한 직후
- **발행자**: `ExtranetSetPriceService` / `AdminAdjustPriceService`
- **구독자 및 처리 내용**:
  - `SearchSnapshotService` -- 해당 숙소의 최저가 정보 재계산 및 갱신
  - `CacheService` -- 가격 관련 캐시 무효화 (Redis 캐시 eviction)

### 2.8 SupplierSyncCompletedEvent

- **발행 시점**: 외부 공급사 재고/가격 동기화 배치가 완료된 직후
- **발행자**: `SyncSupplierInventoryService`
- **구독자 및 처리 내용**:
  - `SearchSnapshotService` -- 외부 공급사 상품의 검색 스냅샷 반영
  - `MonitoringService` -- 동기화 결과(성공/실패 건수, 소요 시간) 기록 및 이상 감지

---

## 3. 구현 전략 (단계별)

### 1단계: Spring ApplicationEvent (현재 적용 가능)

Spring의 내장 이벤트 인프라를 활용한다. 외부 의존성 없이 바로 적용할 수 있다.

**핵심 구성 요소**:
- `ApplicationEventPublisher` -- 유스케이스 서비스에서 이벤트 발행
- `@TransactionalEventListener(phase = AFTER_COMMIT)` -- 트랜잭션 커밋 후 구독자 실행
- `@Async` + `@EventListener` -- 비동기 처리가 필요한 구독자에 적용

**트랜잭션 연동 전략**:
- 재고 차감처럼 데이터 정합성이 중요한 처리 -> `AFTER_COMMIT` 단계에서 동기 실행
- 알림 발송처럼 실패해도 비즈니스에 치명적이지 않은 처리 -> `@Async`로 비동기 실행

**이벤트 발행 위치**: 유스케이스 서비스(application layer)에서 `ApplicationEventPublisher.publishEvent()` 호출. 도메인 모델은 이벤트 객체를 생성만 하고, 발행은 application layer의 책임으로 둔다.

### 2단계: 메시지 브로커 도입 (향후 확장)

서비스가 분리되거나 트래픽이 증가하면 외부 메시지 브로커를 도입한다.

**후보 기술**:
- **Kafka** -- 높은 처리량, 이벤트 로그 영속화, 이벤트 리플레이 가능
- **RabbitMQ** -- 유연한 라우팅, 빠른 도입, 우선순위 큐 지원

**전환 방식**: Outbound Port(`EventPublishPort`)를 정의하고, 1단계에서는 Spring ApplicationEvent 구현체, 2단계에서는 Kafka/RabbitMQ 구현체로 교체한다. 헥사고날 아키텍처의 포트/어댑터 패턴 덕분에 발행자 코드 변경 없이 전환 가능하다.

---

## 4. 이벤트 흐름 다이어그램

### 4.1 예약 생성 -> 결제 -> 확정

```
[Customer]
    |
    v
CustomerCreateReservationService
    |--- publish ---> ReservationCreatedEvent
    |                      |
    |                      +---> InventoryService (재고 홀드)
    |                      +---> NotificationService (접수 알림)
    v
[결제 진행]
    |
    v
CustomerConfirmPaymentService
    |--- publish ---> ReservationConfirmedEvent
                           |
                           +---> PaymentService (결제 확정 기록)
                           +---> NotificationService (확정 알림)
                           +---> ReviewService (리뷰 대상 등록)
```

### 4.2 예약 취소 -> 재고 복구

```
[Customer / HoldExpirationScheduler]
    |
    v
CancelReservation
    |--- publish ---> ReservationCancelledEvent
                           |
                           +---> InventoryService (재고 복구)
                           |         |
                           |         +--- (재고 0 -> 1) ---> InventoryRestoredEvent
                           |                                      |
                           |                                      +---> SearchSnapshotService
                           |
                           +---> PaymentService (환불 처리)
                           +---> NotificationService (취소 알림)
```

### 4.3 가격 변경 -> 캐시 무효화

```
[Partner / Admin]
    |
    v
SetPrice / AdjustPrice Service
    |--- publish ---> PriceChangedEvent
                           |
                           +---> SearchSnapshotService (최저가 재계산)
                           +---> CacheService (가격 캐시 무효화)
```

---

## 5. 설계 고려사항

### 5.1 이벤트 순서 보장

- 1단계(Spring ApplicationEvent): 단일 JVM 내에서 동기 리스너는 등록 순서대로 실행되므로 순서 문제 없음. `@Async` 리스너는 순서 보장 불가 -- 순서가 중요한 처리는 동기로 유지
- 2단계(Kafka): 동일 Partition Key(예: reservationId) 사용 시 파티션 내 순서 보장

### 5.2 멱등성

동일 이벤트가 중복 전달될 수 있다(재시도, 네트워크 이슈 등). 모든 구독자는 멱등하게 구현한다.

- 이벤트에 고유 `eventId`(UUID) 포함
- 구독자는 처리 전 `processed_events` 테이블에서 중복 체크
- 재고 차감 등은 "현재 값에서 -1"이 아닌 "목표 값으로 설정" 방식 검토

### 5.3 실패 처리

- **1단계**: `@TransactionalEventListener` 실패 시 예외 로깅 후 별도 재시도 테이블에 기록. 스케줄러가 주기적으로 재시도
- **2단계**: DLQ(Dead Letter Queue) 활용. 일정 횟수 재시도 후 DLQ로 이동, 운영자가 수동 확인/재처리
- 재시도 정책: 최대 3회, 지수 백오프(1초, 2초, 4초)

### 5.4 이벤트 스키마 버전 관리

- 이벤트 클래스에 `version` 필드 포함 (기본값 1)
- 필드 추가는 하위 호환 가능 -- 구독자가 모르는 필드는 무시
- 필드 삭제/타입 변경은 새 버전 이벤트 클래스로 분리
- 구독자는 `version`에 따라 분기 처리하거나, 신규 버전 구독자를 별도 등록

### 5.5 모니터링

- 이벤트 발행/소비 건수를 메트릭으로 수집
- 이벤트 처리 지연 시간 모니터링
- DLQ 적재 건수 알림 설정
