# Code Convention: OTA Platform Backend

> 아키텍처(헥사고날), 패키지 구조, 클래스 네이밍 컨벤션은 `CLAUDE.md` 참조.
> 이 문서는 **코드 레벨 구현 규칙**에 집중한다.

---

## 1. 명명 규칙 (Naming)

### 기본 규칙
| 대상 | 스타일 | 예시 |
|------|--------|------|
| 클래스 / 인터페이스 | `PascalCase` | `RoomOption`, `InventoryRepository` |
| 메서드 / 변수 | `camelCase` | `calculateTotalPrice()`, `checkInDate` |
| 상수 (`static final`) | `SCREAMING_SNAKE_CASE` | `MAX_RETRY_COUNT`, `DEFAULT_CURRENCY` |
| 패키지 | 소문자, 단어 구분 없음 | `com.accommodation.platform.core.inventory` |

### 구현체 네이밍
- `Impl` 접미어 금지. 기술명 또는 역할을 접두어/접미어로 사용
- Good: `AccommodationJpaAdapter`, `S3ImageUploader`, `ExpediaSupplierAdapter`
- Bad: `AccommodationRepositoryImpl`, `ImageUploaderImpl`

---

## 2. Java 21 & Modern Java 활용

### Record
- DTO(Request/Response), Command/Query, 도메인 이벤트는 `record`로 작성
- 도메인 엔티티, VO는 record 아님 (가변 상태 관리 필요 시)

### Pattern Matching
- `instanceof` 및 `switch` 문에서 Java 21 패턴 매칭 문법 적극 사용

### Sealed Classes
- 도메인 상태 분기 등 제한된 타입 계층이 필요한 경우 활용

### Virtual Threads
- 외부 API 호출(Supplier 연동 등) 대규모 I/O 작업 시 가상 스레드 활용 고려

---

## 3. Lombok 사용 규칙

### 허용
- `@Getter`
- `@Builder` (불변 객체 생성 시)
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` (JPA Entity용)
- `@RequiredArgsConstructor` (생성자 주입)
- `@Slf4j`

### 금지
- `@Data` — equals/hashCode 자동 생성으로 인한 사이드이펙트
- `@Setter` — 객체 불변성 훼손. 명시적 메서드로 상태 변경
- `@AllArgsConstructor` — 필드 순서 변경 시 버그 유발

---

## 4. 핵심 도메인 타입 규칙

### 금액(Money)
- 모든 가격 데이터는 **`BigDecimal`** 사용 (부동소수점 오차 방지)
- `double`, `float` 금지

### 날짜/시간
| 용도 | 타입 | 비고 |
|------|------|------|
| 체크인/체크아웃 날짜 | `LocalDate` | 타임존 무관 — "4월 19일"은 어디서든 4월 19일 |
| 대실 시간 슬롯 | `LocalTime` | 숙소 현지 시간 기준 (14:00 입실 = 그 숙소 현지 14시) |
| 시스템 타임스탬프 (created_at, 이벤트, API 응답 등) | `Instant` | **UTC 단일화**. 프론트엔드가 사용자 로컬 시간대로 변환 |

- `OffsetDateTime`, `ZonedDateTime`은 시스템 타임스탬프에 사용하지 않는다
- 글로벌 확장 시 숙소 엔티티에 `timezone` 필드 추가하여 현지 영업 시간 판단에 활용

### 상태 관리
- 예약 상태, 숙소 상태 등 모든 상태값은 `Enum`으로 관리
- 문자열 상태값 금지

---

## 5. API 디자인

### REST Endpoint
- 복수형 명사 + 하이픈 구분: `/api/v1/accommodations/{id}/rooms`
- 버전은 URL Path에 포함: `/api/v1/...`

### JSON 응답 필드
- `snake_case` 사용 (Jackson `PropertyNamingStrategies.SNAKE_CASE` 설정)

### 공통 응답 포맷
```java
public record ApiResponse<T>(
    String status,       // SUCCESS, ERROR
    T data,
    ErrorDetail error,
    Instant timestamp
) {}

public record ErrorDetail(
    String code,         // 비즈니스 에러 코드
    String message,      // 사용자 노출 메시지
    List<FieldError> fieldErrors  // 필드별 검증 오류 (nullable)
) {}

public record FieldError(
    String field,
    String message
) {}
```

모든 API 응답은 `ApiResponse<T>`로 감싸서 반환한다.

#### 성공 응답 예시
```json
{
  "status": "SUCCESS",
  "data": {
    "reservation_id": 12345,
    "accommodation_name": "서울 호텔",
    "check_in_date": "2026-04-20"
  },
  "error": null,
  "timestamp": "2026-04-19T06:30:00Z"
}
```

#### 에러 응답 예시 - 비즈니스 예외
```json
{
  "status": "ERROR",
  "data": null,
  "error": {
    "code": "INVENTORY_NOT_AVAILABLE",
    "message": "선택하신 날짜에 예약 가능한 객실이 없습니다.",
    "field_errors": null
  },
  "timestamp": "2026-04-19T06:30:00Z"
}
```

#### 에러 응답 예시 - 입력값 검증 실패
```json
{
  "status": "ERROR",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값을 확인해주세요.",
    "field_errors": [
      { "field": "check_in_date", "message": "체크인 날짜는 오늘 이후여야 합니다." },
      { "field": "guest_count", "message": "투숙 인원은 1명 이상이어야 합니다." }
    ]
  },
  "timestamp": "2026-04-19T06:30:00Z"
}
```

---

## 6. 예외 처리

### 구조
- `ErrorCode` Enum: HTTP 상태 코드 + 비즈니스 에러 코드 관리
- 도메인별 Custom Exception 정의 (예: `InventoryNotAvailableException`)
- `@RestControllerAdvice` 전역 핸들러에서 `ApiResponse` 포맷으로 통일 응답

### 규칙
- 비즈니스 예외는 checked exception 아닌 `RuntimeException` 계열
- 예외 메시지에 민감 정보(비밀번호, 토큰 등) 포함 금지

---

## 7. 로깅

- SLF4J (`@Slf4j`) 사용
- 로그 레벨 구분:
  - `INFO`: 주요 비즈니스 흐름 (예약 확정, 재고 변경)
  - `WARN`: 예상 가능한 예외 상황 (재고 부족, 유효하지 않은 요청)
  - `ERROR`: 시스템 오류, 외부 API 장애
- 요청 추적을 위해 `trace_id` 포함 (MDC 활용)

---

## 8. 데이터베이스 & 영속성

### 공통 컬럼
- 모든 테이블: `created_at`, `updated_at` 필수
- 삭제 전략: `is_deleted` (Boolean) + `deleted_at` Soft Delete 적용

### 외래키(FK) 제약
- **FK 제약조건 사용하지 않음** (실무 스타일)
- 참조 무결성은 애플리케이션 레이어(Service)에서 검증
- 조인 성능을 위한 **인덱스는 반드시 생성**
```sql
-- FK는 안 걸지만 조인 컬럼에 인덱스 필수
CREATE INDEX idx_room_accommodation_id ON room(accommodation_id);
CREATE INDEX idx_inventory_room_option_id ON inventory(room_option_id);
```

### 인덱스
- 조회 빈도 높은 필드에 인덱스 설정 (숙소 지역, 날짜, 상태 등)
- 복합 인덱스는 카디널리티 높은 컬럼 우선 배치
- FK 대체 인덱스: 모든 참조 컬럼(`_id` 접미어)에 인덱스 생성

### JPA 규칙
- JPA Entity는 `adapter/out/persistence` 레이어에서만 사용
- 도메인 모델 <-> JPA Entity 변환은 매퍼에서 처리
- N+1 방지: fetch join 또는 EntityGraph 활용
- JPA Entity 간 연관관계는 `@ManyToOne` 대신 **ID 필드(`Long accommodationId`)로 참조** — 도메인 간 결합도 최소화

---

## 9. 동시성 & 성능

### 오버부킹 방지
- 동일 재고 동시 예약: **Pessimistic Lock(DB)** 또는 **Distributed Lock(Redis)** 적용
- 원자성 보장 필수

### 조회 최적화
- 대량 요금 조회: 캐싱 전략 (Redis)
- 읽기/쓰기 분리 고려 (CQRS 패턴)

---

## 10. 테스트

### 구조
- **Given - When - Then** 패턴 준수
- 테스트 메서드명은 **한글** 사용하여 가독성 확보

```java
@Test
void 동시_예약_요청_시_하나만_성공해야_한다() {
    // given
    // when
    // then
}
```

### 테스트 종류
| 종류 | 도구 | 대상 |
|------|------|------|
| 단위 테스트 | JUnit 5 + Mockito | 도메인 서비스, 유스케이스 |
| 통합 테스트 | Testcontainers | DB 연동 흐름 |
| 동시성 테스트 | `CountDownLatch`, `ExecutorService` | 재고 차감, 예약 경합 |
| API 문서 | Spring REST Docs | Controller 테스트 기반 자동 문서 생성 |

---

## 11. 보안

- Spring Security 기반, 역할 기반 접근 제어 (RBAC)
- 채널별 접근 권한 분리: admin / extranet / customer
- 회원: 예약에 필요한 최소 식별자만 보유 (이름, 연락처, 이메일)
