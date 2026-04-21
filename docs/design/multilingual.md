# 다국어(Multilingual) 설계 문서

## 설계 목표

OTA 플랫폼은 한국어, 영어, 일본어 사용자를 동시에 지원한다. 숙소명, 설명, 편의시설명 등 사용자에게 노출되는 텍스트를 단일 테이블에 모두 넣으면 컬럼이 폭발적으로 증가하고 언어 추가 시 스키마 변경이 필요하다.

이를 피하기 위해 **번역 전용 테이블을 분리**하는 방식을 채택한다.

- 원본 엔티티(`accommodation`, `room` 등)는 비즈니스 데이터만 보유
- 언어별 텍스트는 `_translation` 테이블에 `(entity_id, locale)` 복합 키로 관리
- 신규 언어 추가 시 데이터 INSERT만으로 대응 가능 (스키마 변경 없음)
- API 요청의 `Accept-Language` 헤더를 기반으로 적절한 번역을 반환하고, 번역이 없으면 기본 locale(`ko`)로 폴백

---

## 다국어 테이블 구조

### accommodation_translation

```sql
CREATE TABLE accommodation_translation (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_id    BIGINT          NOT NULL COMMENT '숙소 ID (accommodation.id 참조)',
    locale              VARCHAR(10)     NOT NULL COMMENT '언어 코드 (ko, en, ja)',
    name                VARCHAR(255)    NOT NULL COMMENT '숙소명 번역',
    description         TEXT            COMMENT '숙소 설명 번역',
    created_at          DATETIME(6)     NOT NULL,
    updated_at          DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_accommodation_translation (accommodation_id, locale),
    INDEX idx_accommodation_translation_accommodation_id (accommodation_id)
) COMMENT='숙소 다국어 번역 테이블';
```

### room_translation

```sql
CREATE TABLE room_translation (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    room_id     BIGINT          NOT NULL COMMENT '객실 ID (room.id 참조)',
    locale      VARCHAR(10)     NOT NULL COMMENT '언어 코드 (ko, en, ja)',
    name        VARCHAR(255)    NOT NULL COMMENT '객실명 번역',
    description TEXT            COMMENT '객실 설명 번역',
    created_at  DATETIME(6)     NOT NULL,
    updated_at  DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_room_translation (room_id, locale),
    INDEX idx_room_translation_room_id (room_id)
) COMMENT='객실 다국어 번역 테이블';
```

### accommodation_supported_locale

```sql
CREATE TABLE accommodation_supported_locale (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_id    BIGINT          NOT NULL COMMENT '숙소 ID',
    locale              VARCHAR(10)     NOT NULL COMMENT '지원 언어 코드',
    is_default          BOOLEAN         NOT NULL DEFAULT FALSE COMMENT '기본 언어 여부',
    created_at          DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_accommodation_supported_locale (accommodation_id, locale),
    INDEX idx_supported_locale_accommodation_id (accommodation_id)
) COMMENT='숙소별 지원 언어 목록';
```

---

## 언어 코드 규칙

- **소문자 ISO 639-1** 코드만 사용한다.
- `Accept-Language: ko-KR` 처럼 지역 변형이 오면 앞의 언어 코드만 추출한다 (`ko`).

| 언어 | 코드 |
|------|------|
| 한국어 | `ko` |
| 영어 | `en` |
| 일본어 | `ja` |

---

## 실제 저장 예시 (SQL INSERT)

```sql
-- 숙소 기본 정보 (번역과 분리된 비즈니스 데이터)
INSERT INTO accommodation (id, accommodation_type, star_rating, status, created_at, updated_at)
VALUES (1, 'HOTEL', 5, 'ACTIVE', NOW(), NOW());

-- 숙소별 지원 언어 등록
INSERT INTO accommodation_supported_locale (accommodation_id, locale, is_default, created_at)
VALUES
    (1, 'ko', TRUE,  NOW()),  -- 한국어: 기본 언어
    (1, 'en', FALSE, NOW()),  -- 영어
    (1, 'ja', FALSE, NOW());  -- 일본어

-- 숙소명/설명 다국어 번역 데이터
INSERT INTO accommodation_translation (accommodation_id, locale, name, description, created_at, updated_at)
VALUES
    -- 한국어
    (1, 'ko', '그랜드 서울 호텔',
     '서울 도심에 위치한 5성급 럭셔리 호텔입니다. 한강 뷰와 최고급 시설을 제공합니다.',
     NOW(), NOW()),
    -- 영어
    (1, 'en', 'Grand Seoul Hotel',
     'A 5-star luxury hotel located in the heart of Seoul. Enjoy stunning Han River views and world-class amenities.',
     NOW(), NOW()),
    -- 일본어
    (1, 'ja', 'グランドソウルホテル',
     'ソウル中心部に位置する5つ星のラグジュアリーホテルです。漢江の絶景と最高級の設備をご提供します。',
     NOW(), NOW());

-- 객실 번역 데이터
INSERT INTO room_translation (room_id, locale, name, description, created_at, updated_at)
VALUES
    (1, 'ko', '디럭스 더블룸', '한강 뷰가 보이는 넓은 더블룸입니다.', NOW(), NOW()),
    (1, 'en', 'Deluxe Double Room', 'A spacious double room with Han River view.', NOW(), NOW()),
    (1, 'ja', 'デラックスダブルルーム', '漢江の景色が見える広々としたダブルルームです。', NOW(), NOW());
```

---

## 향후 API 응답 흐름 (의사코드)

실제 구현 전 설계 방향을 보여주는 의사코드다.

```
[HTTP 요청]
GET /api/v1/accommodations/1
Accept-Language: en

[1단계] locale 추출 (Adapter in/web)
  locale = parseLocale(request.getHeader("Accept-Language"))
  // "en-US" → "en", "ja" → "ja", 파싱 실패 → "ko" (기본값)

[2단계] 숙소 기본 데이터 조회 (Application)
  accommodation = loadAccommodationPort.findById(accommodationId)

[3단계] 번역 조회 with 폴백 (Application)
  translation = loadAccommodationTranslationPort.findByAccommodationIdAndLocale(
      accommodationId, locale
  )
  if (translation == null) {
      // 요청 언어 번역 없음 → 기본 locale(ko)로 폴백
      translation = loadAccommodationTranslationPort.findByAccommodationIdAndLocale(
          accommodationId, DEFAULT_LOCALE  // "ko"
      )
  }

[4단계] 응답 조립 (Adapter in/web)
  response = SearchAccommodationResponse.of(accommodation, translation)
  return ApiResponse.success(response)

[응답 예시]
{
  "status": "SUCCESS",
  "data": {
    "accommodation_id": 1,
    "name": "Grand Seoul Hotel",         // 영어 번역 적용
    "description": "A 5-star luxury hotel located in the heart of Seoul.",
    "star_rating": 5
  },
  "error": null,
  "timestamp": "2026-04-21T06:00:00Z"
}
```

### 폴백 정책 요약

| 시나리오 | 동작 |
|---------|------|
| 요청 언어(`en`) 번역 존재 | 해당 번역 반환 |
| 요청 언어 번역 없음 | 기본 locale(`ko`) 번역으로 폴백 |
| 기본 locale 번역도 없음 | 빈 문자열 또는 `null` 반환 (데이터 정합성 이슈로 별도 알림) |
| `Accept-Language` 헤더 없음 | 기본 locale(`ko`) 사용 |

---

## 지원 언어 확장 방법

신규 언어 추가 시 **스키마 변경 없이** 아래 3단계만 수행한다.

### 1단계 — 번역 데이터 INSERT

```sql
-- 예: 중국어 간체(zh) 추가
INSERT INTO accommodation_supported_locale (accommodation_id, locale, is_default, created_at)
VALUES (1, 'zh', FALSE, NOW());

INSERT INTO accommodation_translation (accommodation_id, locale, name, description, created_at, updated_at)
VALUES (1, 'zh', '首尔大酒店', '位于首尔市中心的五星级豪华酒店。', NOW(), NOW());
```

### 2단계 — 언어 코드 Enum 추가 (애플리케이션)

```java
// 현재
public enum SupportedLocale {
    KO("ko"), EN("en"), JA("ja");

    // 추가
    ZH("zh");
}
```

### 3단계 — 번역 작업 (운영)

번역 전문 업체 또는 내부 번역팀이 기존 콘텐츠를 번역하여 `_translation` 테이블에 일괄 INSERT.  
파트너 센터(extranet)에 언어별 번역 입력 UI를 제공하면 파트너가 직접 관리 가능.

---

## 구현 현황

| 항목 | 상태 |
|------|------|
| `AccommodationTranslationJpaEntity` | 구현 완료 |
| `RoomTranslationJpaEntity` | 구현 완료 |
| `accommodation_supported_locale` 테이블 | 구현 완료 |
| API 응답에 번역 적용 | 향후 구현 예정 |
| `Accept-Language` 헤더 파싱 미들웨어 | 향후 구현 예정 |
