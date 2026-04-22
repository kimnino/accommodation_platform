# Database Design Reference

OTA 숙박 플랫폼 데이터베이스 설계 문서.  
실제 DDL은 [`docs/design/schema.sql`](./schema.sql) 참고.

---

## 설계 원칙

| 원칙 | 내용 |
|------|------|
| **FK 제약 미사용** | 실무 스타일. 참조 무결성은 서비스 레이어에서 검증. 조인 컬럼에 인덱스로 대체 |
| **Soft Delete** | `is_deleted(TINYINT) + deleted_at(DATETIME)` 패턴. 물리 삭제 금지 |
| **공통 감사 컬럼** | 모든 테이블에 `created_at`, `updated_at` 필수 |
| **금액 타입** | `DECIMAL(12,2)` — BigDecimal 매핑, float/double 금지 |
| **타임스탬프** | `DATETIME(6)` UTC — Instant 매핑. 프론트가 로컬 시간으로 변환 |
| **날짜** | `DATE` — LocalDate 매핑 (타임존 무관) |
| **시간** | `TIME` — LocalTime 매핑 (숙소 현지 시간 기준) |
| **상태값** | 모든 상태는 `VARCHAR` Enum. 허용 값은 컬럼 주석 참조 |
| **다국어** | `{entity}_translation` 테이블 분리. `ko` 요청은 원본 테이블 직접 사용, 번역 조회 스킵 |

---

## 도메인 구성

```
1.  Member                     회원
2.  Accommodation              숙소 (이미지 · 번역 · 지역 · 지원언어 · 수정요청 · 대실설정)
3.  Room                       객실 (이미지 · 번역 · 옵션 · 옵션번역)
4.  Inventory                  재고 (숙박 날짜별 · 대실 슬롯)
5.  Price                      요금 (숙박 · 대실)
6.  Reservation                예약
7.  Payment                    결제  ★ 설계 완료 / 유스케이스 미구현
8.  Review                     리뷰  ★ 설계 완료 / 유스케이스 미구현
9.  Coupon                     쿠폰  ★ 설계 완료 / 유스케이스 미구현
10. Tag                        동적 태그 시스템
11. Supplier                   외부 공급사 연동
12. Wishlist                   찜    ★ 설계 완료 / 유스케이스 미구현
```

---

## 테이블 상세

### 1. Member (회원)

> 예약에 필요한 최소 식별 정보만 보유. 상세 회원 서비스는 미구현.

**`member`**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | 자동 증가 |
| name | VARCHAR(100) | 이름 |
| phone | VARCHAR(20) | 전화번호 |
| email | VARCHAR(255) UNIQUE | 이메일 (로그인 식별자) |
| role | VARCHAR(20) | `CUSTOMER` \| `PARTNER` \| `ADMIN` |
| status | VARCHAR(20) | `ACTIVE` \| `SUSPENDED` \| `WITHDRAWN` |
| is_deleted | TINYINT(1) | Soft Delete 플래그 |
| deleted_at | DATETIME(6) | 탈퇴 시각 |

**인덱스:** `idx_member_email(email)`

---

### 2. Accommodation (숙소)

#### `accommodation`

> 숙소 마스터. 파트너가 등록하며 관리자 승인 후 ACTIVE.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | 자동 증가 |
| partner_id | BIGINT | member.id (PARTNER 역할) — FK 없음, 인덱스만 |
| name | VARCHAR(255) | 숙소명 (기본 한국어) |
| type | VARCHAR(30) | `HOTEL` \| `RESORT` \| `GUEST_HOUSE` \| `MOTEL` \| `PENSION` \| `POOL_VILLA` |
| region_id | BIGINT | accommodation_region.id — null 허용 |
| full_address | VARCHAR(500) | 전체 주소 |
| latitude | DOUBLE | 위도 |
| longitude | DOUBLE | 경도 |
| location_description | VARCHAR(1000) | 위치 설명 |
| status | VARCHAR(30) | `PENDING_APPROVAL` → `ACTIVE` → `SUSPENDED` \| `CLOSED` |
| check_in_time | TIME | 기본 체크인 시간 (null 허용) |
| check_out_time | TIME | 기본 체크아웃 시간 (null 허용) |
| supplier_managed | TINYINT(1) | 외부 공급사 관리 여부 |

**인덱스:** `idx_acc_partner_id`, `idx_acc_region_id`, `idx_acc_type_status(type, status)`

---

#### `accommodation_image`

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_id | BIGINT | 숙소 ID |
| relative_path | VARCHAR(500) | 상대경로 (예: `/accommodation/exterior/20260420.png`) |
| category | VARCHAR(30) | `EXTERIOR` \| `LOBBY` \| `ROOM` \| `POOL` \| `RESTAURANT` \| `FACILITY` |
| display_order | INT | 노출 순서 |
| is_primary | TINYINT(1) | 대표 이미지 여부 |

---

#### `accommodation_translation`

> 숙소의 다국어 번역. 지원 언어: `ko`, `en`, `ja`. `ko` 요청은 이 테이블을 조회하지 않음.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_id | BIGINT | 숙소 ID |
| locale | VARCHAR(10) | 언어 코드 (`ko` \| `en` \| `ja`) |
| name | VARCHAR(255) | 번역된 숙소명 |
| full_address | VARCHAR(500) | 번역된 주소 |
| location_description | VARCHAR(1000) | 번역된 위치 설명 |
| introduction | TEXT | 소개 번역 |
| service_and_facilities | TEXT | 서비스·시설 번역 |
| usage_info | TEXT | 이용 안내 번역 |
| cancellation_and_refund_policy | TEXT | 취소·환불 정책 번역 |

**유니크:** `(accommodation_id, locale)`

---

#### `accommodation_supported_locale`

> 숙소별 지원 언어 목록. 이 테이블에 등록된 언어만 번역 관리 가능.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_id | BIGINT | 숙소 ID |
| locale | VARCHAR(10) | 지원 언어 코드 |

**유니크:** `(accommodation_id, locale)`

---

#### `accommodation_modification_request`

> 파트너가 숙소 정보 수정 요청 → 관리자 승인 후 반영. 수정 데이터는 JSON으로 보관.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_id | BIGINT | 대상 숙소 ID |
| partner_id | BIGINT | 요청 파트너 ID |
| status | VARCHAR(20) | `PENDING` → `APPROVED` \| `REJECTED` |
| request_data | JSON | 수정 내용 (`UpdateAccommodationCommand` 직렬화) |
| rejection_reason | VARCHAR(1000) | 반려 사유 (REJECTED 시 필수) |

---

#### `accommodation_region`

> 숙소 유형별 검색용 지역 마스터. 계층 구조(parent_id) 지원.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_type | VARCHAR(30) | 적용 숙소 유형 |
| region_name | VARCHAR(100) | 지역명 |
| parent_id | BIGINT | 상위 지역 ID (null = 최상위) |
| sort_order | INT | 노출 순서 |

---

#### `accommodation_hourly_setting`

> 숙소별 대실(시간 예약) 운영 설정. 숙소당 1개.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_id | BIGINT UNIQUE | 숙소 ID (1:1) |
| operating_start_time | TIME | 대실 운영 시작 시간 (예: `10:00`) |
| operating_end_time | TIME | 대실 운영 종료 시간 (예: `22:00`) |
| usage_duration_minutes | INT | 1회 이용 시간(분) — 예: 120 |
| buffer_minutes | INT | 청소·정비 시간(분) |
| slot_unit_minutes | INT | 슬롯 단위 분 (30 or 60) |

---

### 3. Room (객실)

#### `room`

> 숙소 내 객실. 같은 타입이라도 물리적으로 별개인 객실은 row 분리.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| accommodation_id | BIGINT | 소속 숙소 ID |
| name | VARCHAR(255) | 객실명 (예: 스탠다드 더블) |
| room_type_name | VARCHAR(100) | 객실 유형명 |
| standard_capacity | INT | 기준 인원 |
| max_capacity | INT | 최대 인원 |
| status | VARCHAR(20) | `ACTIVE` \| `INACTIVE` |

---

#### `room_option`

> 같은 객실의 상품 분기. 예: 조식 포함/미포함, 대실 옵션.
> **재고·요금은 객실이 아니라 옵션 단위로 관리.**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| room_id | BIGINT | 소속 객실 ID |
| name | VARCHAR(255) | 옵션명 (예: 조식 포함) |
| cancellation_policy | VARCHAR(30) | `FREE_CANCELLATION` \| `NON_REFUNDABLE` \| `PARTIAL_REFUND` |
| additional_price | DECIMAL(12,2) | 기본 요금 대비 추가 금액 |
| hourly_start_time | TIME | 대실 가능 시작 시간 (null = 제한 없음) |
| hourly_end_time | TIME | 대실 가능 종료 시간 (null = 제한 없음) |
| check_in_time | TIME | **옵션별 체크인 재정의** (null → 숙소 기본값 사용) |
| check_out_time | TIME | **옵션별 체크아웃 재정의** (null → 숙소 기본값 사용, 레이트 체크아웃 등) |

> **check_in/out_time 우선순위:** 옵션 값 → 숙소 기본값 → null  
> 레이트 체크아웃 옵션은 이 필드로 숙소 기본값을 덮어쓴다.

---

#### `room_image` / `room_translation` / `room_option_translation`

| 테이블 | 설명 |
|--------|------|
| room_image | 객실 이미지. `display_order`, `is_primary` |
| room_translation | 객실명·유형명 다국어 번역. `(room_id, locale)` 유니크 |
| room_option_translation | 옵션명 다국어 번역. `(room_option_id, locale)` 유니크 |

---

### 4. Inventory (재고)

#### `inventory` — 숙박 재고 (날짜별)

> **날짜 × 옵션** 단위. 파트너가 일별로 설정. 예약 시 `remaining_quantity` 감소 (Pessimistic Lock).

| 컬럼 | 타입 | 설명 |
|------|------|------|
| room_option_id | BIGINT | 옵션 ID |
| date | DATE | 해당 날짜 |
| total_quantity | INT | 총 재고 수 |
| remaining_quantity | INT | 잔여 재고 수 |
| status | VARCHAR(20) | `AVAILABLE` \| `SOLD_OUT` |

**유니크:** `(room_option_id, date)`  
**인덱스:** `idx_inventory_option_date(room_option_id, date)`

---

#### `time_slot_inventory` — 대실 슬롯 재고

> **날짜 × 슬롯 시작시간 × 옵션** 단위. `slot_time=14:00`은 14:00~14:30 구간을 의미.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| room_option_id | BIGINT | 옵션 ID |
| date | DATE | 해당 날짜 |
| slot_time | TIME | 슬롯 시작 시간 (30분 단위) |
| status | VARCHAR(20) | `AVAILABLE` \| `OCCUPIED` \| `BLOCKED` |

**유니크:** `(room_option_id, date, slot_time)`

> **STAY ↔ HOURLY 충돌 처리:** 숙박 예약으로 `remaining_quantity=0`이 되면,
> 동일 날짜의 `time_slot_inventory` 슬롯을 `BLOCKED`로 전환해야 한다. (설계 확정, 구현 예정)

---

### 5. Price (요금)

#### `room_price`

> 날짜별 요금. `price_type`으로 숙박/대실 구분. 파트너가 일별 설정.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| room_option_id | BIGINT | 옵션 ID |
| date | DATE | 해당 날짜 |
| price_type | VARCHAR(20) | `STAY` \| `HOURLY` |
| base_price | DECIMAL(12,2) | 정상가 |
| selling_price | DECIMAL(12,2) | 판매가 (할인 적용) |
| tax_included | TINYINT(1) | 세금 포함 여부 (기본 true) |

**유니크:** `(room_option_id, date, price_type)`  
**인덱스:** `idx_room_price_option_type_date(room_option_id, price_type, date)`

---

### 6. Reservation (예약)

#### `reservation`

> 예약의 핵심 테이블. 숙박(STAY)과 대실(HOURLY)을 동일 테이블에서 관리.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| reservation_number | VARCHAR(50) UNIQUE | 노출용 예약 번호 (`RSV-{timestamp}-{random}`) |
| reservation_request_id | VARCHAR(100) UNIQUE | **멱등성 키** — 클라이언트가 생성하는 UUID. 중복 요청 방어 |
| member_id | BIGINT | 예약 회원 ID |
| accommodation_id | BIGINT | 숙소 ID |
| room_option_id | BIGINT | 예약된 옵션 ID |
| reservation_type | VARCHAR(10) | `STAY` \| `HOURLY` |
| check_in_date | DATE | 체크인 날짜 |
| check_out_date | DATE | 체크아웃 날짜 (대실은 null) |
| hourly_start_time | TIME | 대실 시작 시간 (숙박은 null) |
| hourly_usage_minutes | INT | 대실 이용 시간(분) |
| guest_name | VARCHAR(100) | 투숙객 이름 |
| guest_phone | VARCHAR(20) | 투숙객 연락처 |
| guest_email | VARCHAR(255) | 투숙객 이메일 |
| total_price | DECIMAL(12,2) | 최종 결제 금액 |
| status | VARCHAR(20) | `PAYMENT_WAITING` → `CONFIRMED` → `CHECKED_IN` → `CHECKED_OUT` \| `CANCELLED` |
| hold_expired_at | DATETIME(6) | 결제 대기 만료 시각 (Hold 만료 시 자동 취소 스케줄러가 처리) |

**동시성 처리 3중 방어:**
1. `reservation_request_id` — 멱등성 키로 중복 요청 차단
2. Pessimistic Lock — `inventory.remaining_quantity` 감소 시 DB 락
3. `hold_expired_at` 스케줄러 — 결제 미완료 예약 자동 취소 후 재고 복구

---

### 7. Payment (결제) ★ 설계 완료 / 미구현

#### `payment`

| 컬럼 | 타입 | 설명 |
|------|------|------|
| reservation_id | BIGINT | 예약 ID (1:1 권장) |
| amount | DECIMAL(19,4) | 결제 금액 |
| payment_method | VARCHAR(30) | `CARD` \| `KAKAO_PAY` \| `NAVER_PAY` \| `TOSS` \| `BANK_TRANSFER` |
| status | VARCHAR(20) | `PENDING` → `COMPLETED` \| `REFUNDED` \| `FAILED` |
| pg_transaction_id | VARCHAR(200) | PG사 거래 ID |
| paid_at | DATETIME(6) | 결제 완료 시각 |
| cancelled_at | DATETIME(6) | 취소 시각 |

> **미구현 이유:** PG사 연동 API 설계가 필요. 예약 흐름(PAYMENT_WAITING → CONFIRMED)에 결제 이벤트 연결 예정.

---

### 8. Review (리뷰) ★ 설계 완료 / 미구현

#### `review`

| 컬럼 | 타입 | 설명 |
|------|------|------|
| reservation_id | BIGINT UNIQUE | 예약 ID — 1예약 1리뷰 제한 |
| member_id | BIGINT | 작성자 ID |
| accommodation_id | BIGINT | 숙소 ID (검색 인덱스용 비정규화) |
| rating | DECIMAL(3,1) | 평점 1.0 ~ 5.0 |
| content | TEXT | 리뷰 본문 |
| is_visible | TINYINT(1) | 관리자 노출 관리 플래그 |

#### `review_image`

| 컬럼 | 타입 | 설명 |
|------|------|------|
| review_id | BIGINT | 리뷰 ID |
| relative_path | VARCHAR(500) | 이미지 상대경로 |
| display_order | INT | 노출 순서 |

---

### 9. Coupon (쿠폰) ★ 설계 완료 / 미구현

#### `coupon`

| 컬럼 | 타입 | 설명 |
|------|------|------|
| code | VARCHAR(50) UNIQUE | 쿠폰 코드 |
| discount_type | VARCHAR(20) | `PERCENTAGE` \| `FIXED_AMOUNT` |
| discount_amount | DECIMAL(19,4) | 할인 금액 또는 비율 |
| minimum_order_amount | DECIMAL(19,4) | 최소 주문 금액 |
| maximum_discount_amount | DECIMAL(19,4) | 퍼센트 할인 최대 캡 (null = 제한 없음) |
| valid_from / valid_to | DATETIME(6) | 유효 기간 |
| usage_limit | INT | 총 사용 가능 횟수 (0 = 무제한) |
| used_count | INT | 사용된 횟수 |

#### `member_coupon`

> 회원이 보유한 쿠폰. `is_used`로 사용 여부 추적.

#### `coupon_accommodation_mapping`

> 특정 숙소에만 사용 가능한 쿠폰 제한. 레코드 없으면 전체 숙소 적용.

---

### 10. Tag (동적 태그 시스템)

> 하드코딩 없이 관리자가 태그 그룹·태그를 동적으로 구성. 숙소/객실에 매핑.

```
tag_group (수영장 보유, 조식 서비스, 테마 등)
  └── tag (야외 수영장, 실내 수영장 / 조식 포함, 뷔페 등)
        ├── accommodation_tag (숙소 ↔ 태그)
        └── room_tag          (객실 ↔ 태그)
```

| 테이블 | 설명 |
|--------|------|
| tag_group | 태그 분류 그룹. `supplier_id=0`은 플랫폼 자체 태그 |
| tag_group_translation | 그룹명 다국어 번역 |
| tag | 개별 태그. `tag_group_id`로 그룹 소속 |
| tag_translation | 태그명 다국어 번역 |
| accommodation_tag | 숙소-태그 N:M 매핑 |
| room_tag | 객실-태그 N:M 매핑 |

---

### 11. Supplier (외부 공급사 연동)

> 외부 OTA(Expedia 등)의 숙소를 플랫폼 데이터로 통합. 어댑터 패턴으로 구현.

```
supplier
  ├── supplier_accommodation_mapping   (외부 숙소 ID ↔ 플랫폼 숙소 ID)
  ├── supplier_room_mapping            (외부 객실 ID ↔ 플랫폼 객실 ID)
  ├── supplier_room_option_mapping     (외부 옵션 ID ↔ 플랫폼 옵션 ID)
  └── supplier_category_mapping        (외부 카테고리 ↔ 플랫폼 태그)
```

| 테이블 | 설명 |
|--------|------|
| supplier | 공급사 마스터. `code`로 식별 (예: `EXPEDIA`, `BOOKING_COM`) |
| supplier_accommodation_mapping | 공급사 외부 ID ↔ 플랫폼 ID 양방향 매핑. `live_price_sync` = 실시간 요금 동기화 여부 |
| supplier_category_mapping | 공급사 카테고리를 플랫폼 tag에 매핑. 신규 카테고리는 `internal_tag_id=null`로 수집 후 관리자가 매핑 |

---

### 12. Wishlist (찜) ★ 설계 완료 / 미구현

#### `wishlist`

| 컬럼 | 타입 | 설명 |
|------|------|------|
| member_id | BIGINT | 회원 ID |
| accommodation_id | BIGINT | 숙소 ID |

**유니크:** `(member_id, accommodation_id)` — 동일 숙소 중복 찜 방지

---

## 테이블 관계 요약

```
member ──────────────────────── reservation ─────── room_option
  │                                                      │
  │ (partner_id)                                    room_option ─── room_price
  │                                                      │
accommodation                                       inventory
  ├── accommodation_image                           time_slot_inventory
  ├── accommodation_translation
  ├── accommodation_supported_locale
  ├── accommodation_modification_request
  ├── accommodation_region
  ├── accommodation_hourly_setting
  └── room
        ├── room_image
        ├── room_translation
        └── room_option
              ├── room_option_translation
              ├── inventory
              ├── time_slot_inventory
              └── room_price

reservation ─── payment
reservation ─── review ─── review_image

member ─── member_coupon ─── coupon ─── coupon_accommodation_mapping
member ─── wishlist

accommodation ─── accommodation_tag ─── tag ─── tag_group
room          ─── room_tag          ─── tag

supplier ─── supplier_accommodation_mapping ─── accommodation
         ─── supplier_room_mapping          ─── room
         ─── supplier_room_option_mapping   ─── room_option
         ─── supplier_category_mapping      ─── tag
```

---

## 핵심 설계 결정

### 재고를 옵션 단위로 관리하는 이유

- 동일 객실에 "조식 포함"과 "조식 미포함" 옵션이 독립적인 재고를 가질 수 있다
- 옵션 단위 재고 설정 시 파트너 유연성 극대화

### 숙박/대실 재고 분리

- `inventory`: 날짜별 잔여 수량 — 숙박 예약 시 감소
- `time_slot_inventory`: 30분 슬롯별 상태 — 대실 예약 시 OCCUPIED 전환
- 두 테이블은 별도 관리. 같은 날 숙박+대실 동시 예약 가능 (재고가 2개 이상이면)
- 충돌 방지: 숙박으로 재고 소진(`remaining_quantity=0`) 시 해당 날짜 슬롯을 `BLOCKED`로 전환 (구현 예정)

### 수정 승인 워크플로우

- 파트너 수정 요청 → `accommodation_modification_request` 생성 (request_data: JSON)
- 관리자 승인 → 원본 `accommodation` 테이블 반영
- 거절 → `rejection_reason` 기록, 원본 불변
- **장점:** 운영 중 데이터 무결성 보장, 감사 추적 가능

### FK 없이 인덱스만 사용하는 이유

- 대용량 트래픽 환경에서 FK 제약이 INSERT/DELETE 성능 저하 유발
- 참조 무결성을 서비스 레이어에서 관리하면 마이크로서비스 분리 시 유리
- 모든 참조 컬럼(`_id` 접미어)에 인덱스 생성으로 조인 성능 확보

### 멱등성 키 (`reservation_request_id`)

- 클라이언트가 UUID를 생성해서 요청에 포함
- DB UNIQUE 제약으로 동일 키 재처리 방어
- 네트워크 재시도로 인한 이중 예약 방지

---

## 인덱스 전략

| 패턴 | 적용 컬럼 예시 | 목적 |
|------|---------------|------|
| 단일 조인 컬럼 | `accommodation_id`, `room_id`, `member_id` | FK 대체 |
| 복합 조회 | `(type, status)`, `(room_option_id, date)` | 필터 + 범위 쿼리 최적화 |
| 유니크 비즈니스 키 | `(room_option_id, date, price_type)` | 중복 방지 + 조회 속도 |
| 상태 필터 | `status` on reservation, payment | 상태별 배치 처리 |
| 만료 시간 | `hold_expired_at` on reservation | 스케줄러 배치 조회 |
