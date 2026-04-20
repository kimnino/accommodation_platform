₩
## 2026/04/20 - [Phase 0 공통 인프라 구현]

### 수행 내용

1. 작업계획서에 브랜치 전략 추가
   - Phase별 feature 브랜치 네이밍 및 머지 순서 명시
   - 작업 요청 시 올바른 브랜치로 자동 checkout 규칙 추가
2. `feat/phase0-common-infra` 브랜치 생성 및 Phase 0 구현
   - `build.gradle` 의존성 보강 (validation, testcontainers BOM)
   - `application.yaml` 설정 (datasource, JPA validate/open-in-view:false, Jackson SNAKE_CASE, 로깅 패턴)
   - 공통 응답 포맷: `ApiResponse`, `ErrorDetail`, `FieldError` (record)
   - 예외 처리: `BusinessException`, `ErrorCode` Enum, `GlobalExceptionHandler` (@RestControllerAdvice)
   - 도메인 베이스: `BaseEntity` (순수 Java, Instant), `SoftDeletable` 인터페이스
   - JPA 베이스: `BaseJpaEntity` (@MappedSuperclass, @PrePersist/@PreUpdate)
   - 설정 클래스: `JacksonConfig` (SNAKE_CASE), `WebMvcConfig` (CORS)
   - 테스트 인프라: `IntegrationTestBase` (Testcontainers MySQL), `application-test.yaml`
3. IntelliJ codeStyles 프로젝트 설정 추가
   - 들여쓰기 스페이스 4칸, 최대 줄 길이 120자
   - 와일드카드 import 비활성화, import 순서 규칙 설정
   - 어노테이션 별도 줄, 메서드 체이닝 줄바꿈 등
4. Spring Boot 4.0 / Jackson 3.x 호환 이슈 해결
   - Jackson 패키지 변경 대응 (`com.fasterxml.jackson` → `tools.jackson`)
   - `Jackson2ObjectMapperBuilderCustomizer` → `JsonMapperBuilderCustomizer` 마이그레이션
   - `WRITE_DATES_AS_TIMESTAMPS` 설정 제거 (Jackson 3에서 해당 Enum 값 삭제됨)

### 이슈 / 학습
- Spring Boot 4.0에서 Jackson 3.x를 사용하며 기존 2.x 코드가 호환되지 않음. 패키지명, 자동 설정 클래스명, SerializationFeature Enum 값이 모두 변경됨
- Testcontainers는 Spring Boot BOM에 포함되지 않아 별도 BOM 설정 필요


---

## 2026/04/20 - [Phase 1 숙소/객실 도메인 구현 및 피드백 반영]

### 수행 내용

1. `feat/phase1-accommodation` 브랜치 생성 및 Phase 1 구현
   - Accommodation 도메인 모델 (엔티티, VO, 상태 전환 invariant)
   - Room / RoomOption 도메인 모델
   - Outbound Port 분리: `PersistAccommodationPort` / `LoadAccommodationPort` (Room 동일)
   - Extranet 채널: 숙소 등록, 수정 요청, 조회 / 객실·옵션 등록, 조회
   - Admin 채널: 숙소 승인·정지·폐쇄, 수정 요청 승인·거절
2. 피드백 1차 반영
   - Enum 패키지 이동 (`domain/model/` → `domain/enums/`)
   - 이미지 URL → 상대경로 저장 (`relativePath`)
   - 주소 구조 단순화 (Address VO 삭제 → `fullAddress` + `locationDescription`)
   - AccommodationTranslationJpaEntity 추가 (소개, 시설, 이용정보, 취소규정, 다국어)
   - Port 네이밍: `AccommodationRepository` → `PersistAccommodationPort` + `LoadAccommodationPort`
   - JPA Entity 모든 필드에 한글 설명 주석 추가
3. 피드백 2차 반영
   - `breakfastIncluded` 필드 제거 — 옵션명과 추가금액으로 분기
   - 숙소 수정 승인 구조 추가 (`AccommodationModificationRequest` PENDING → APPROVED/REJECTED)
4. 피드백 3차 반영
   - 숙소명/객실명 다국어 지원 (`AccommodationTranslation.name`, `RoomTranslation` 신규)
   - RoomType 관리 방식 확정: 파트너가 직접 다국어로 입력, 관리자는 심사만 담당
   - `room_type_template` 테이블 삭제 → `roomTypeName` String (nullable) + `room_translation`에서 다국어 관리
5. 피드백 기반 개발 프로세스를 CLAUDE.md에 추가
6. 피드백 4차 반영
   - 숙소파트너 다국어 지원 선택 기능 (`AccommodationSupportedLocaleJpaEntity`)
   - 플랫폼 초기 지원 언어: ko, en, ja
7. 피드백 5차 반영
   - 숙소 등록 API에 다국어 데이터(translations) 입력 추가
   - `PersistAccommodationTranslationPort` 추가, 등록 시 translation 함께 저장
   - 언어코드 소문자 통일 (DB, API 모두 ko/en/ja)
   - 객실/옵션 수정(PUT), 삭제(DELETE) API 구현
8. Phase 1 테스트 코드 완성
   - Room/RoomOption 도메인 단위 테스트
   - ExtranetRoomController REST Docs 테스트 (등록/수정/삭제/목록/옵션)
   - Spring REST Docs HTML 문서 생성 (Extranet 숙소 + 객실 API)

### 이슈 / 학습
- 객실유형(RoomType)을 enum → 관리자 템플릿 → 파트너 자유 입력으로 3번 변경. 도메인 특성상 숙소유형마다 객실유형 체계가 다르고, 같은 유형 내에서도 업체마다 다를 수 있어 자유 입력 + 심사가 가장 현실적
- 수정 승인 구조에서 수정 데이터를 JSON으로 저장하여 유연성 확보. 향후 diff 비교 기능도 가능
- Port를 Persist/Load로 분리하면 서비스에서 필요한 의존성만 주입받을 수 있어 단일 책임 원칙에 부합
- Spring Boot 4에서 `@WebMvcTest`, `@AutoConfigureRestDocs` 패키지가 변경됨 (`org.springframework.boot.webmvc.test.autoconfigure`, `org.springframework.boot.restdocs.test.autoconfigure`)
- REST Docs에서 JSON에 없는 optional 필드는 `.type("String")` 명시 필요

### 개발자 코멘트
1. 처음에 객실타입에 대한 방향을 엉뚱하게 생각했지만, 다국어처리까지 숙소파트너에게 일임하여, 하도록 설정, 추가 피드백으로 숙소파트너가 다국어 서비스를 지원할 지에 대해서 로직 추가 예정

---

## 2026/04/20 - [Phase 1 동적 태그 시스템 구현]

### 수행 내용

1. `feat/phase1-tag-system` 브랜치 생성 및 동적 태그 시스템 구현
   - TagGroup / Tag 도메인 모델 (활성/비활성 상태 전환)
   - JPA Entity: tag_group, tag, accommodation_tag, room_tag
   - Persist/Load Port 분리, TagJpaAdapter 구현
2. Admin 채널 — 태그 관리
   - 태그 그룹 CRUD (생성/수정/비활성화/목록)
   - 태그 CRUD (그룹 내 생성/수정/비활성화/목록)
   - Admin REST Docs 테스트 4개 + admin-api.adoc 최신화
3. Extranet 채널 — 파트너 태그 관리
   - 숙소유형에 맞는 태그 그룹/태그 조회 API
   - 숙소 태그 추가(POST)/삭제(DELETE)/조회(GET) API
   - extranet-api.adoc 최신화
4. 피드백 6차 반영
   - 태그 다국어: tag_group_translation, tag_translation 테이블 추가
   - getTagsByGroup 메서드 partnerId 누락 수정
5. CLAUDE.md 프로젝트 공통 규칙 정리
   - Outbound Port 네이밍 (Persist/Load 분리), Enum 위치 (domain/enums/)
   - 다국어 패턴, 이미지 상대경로, JPA 주석, 수정 승인 구조

### 이슈 / 학습
- LoadTagGroupPort와 LoadTagPort 모두 `findById(Long)` 메서드를 가지면 하나의 Adapter에서 구현 시 충돌. LoadTagPort는 `findTagById(Long)`으로 구분
- TagGroup의 accommodationType이 null이면 전체 숙소유형에 적용 — JPQL에서 `IS NULL OR =` 조건으로 처리

### 개발자 코멘트
1. 

---

## 2026/04/20 - [Phase 2 재고/요금 도메인 구현 및 피드백 반영]

### 수행 내용

1. `feat/phase2-inventory-price` 브랜치 생성 및 Phase 2 구현
   - Inventory 도메인 (숙박 일별 재고, 불변식 `remaining >= 0`, 상태 자동 전환)
   - TimeSlotInventory 도메인 (대실 30/60분 블록, AVAILABLE → OCCUPIED/BLOCKED)
   - InventoryDomainService (연박 검증, 숙박/대실 공존, 버퍼타임, 단축 이용 계산)
   - RoomPrice 도메인 (PriceType STAY/HOURLY, VAT 계산)
   - PriceDomainService (박수 합산, VAT 포함/불포함 처리)
   - 비관적 락 (`@Lock(PESSIMISTIC_WRITE)`) — 예약 동시성 제어용
2. 대실 운영 설정
   - AccommodationHourlySetting (운영시간, 이용시간, 버퍼, 슬롯 단위)
   - 슬롯 단위: 파트너가 30분 또는 60분 선택
   - 날짜별 대실 슬롯 자동 생성 API
3. Extranet 재고/요금 API
   - 재고 설정 (연속/비연속 일자 모두 지원)
   - 요금 설정 (PriceType으로 숙박/대실 가격 분리, 날짜별 차등 가능)
   - 대실 운영 설정 + 슬롯 오픈 API
4. Admin 요금 조정 API
5. 피드백 반영 (3차)
   - 재고 로우 사전 생성 방식 유지 확정
   - 대실 슬롯: 이용시간별 겹치는 슬롯 → 30/60분 단위 블록 타임라인으로 변경
   - 숙박/대실 시간대 공존: 체크인 시간 이후 슬롯만 확인
   - 비연속 일자 재고/가격 설정 (dates 필드)
   - 대실 가격: PriceType(STAY/HOURLY) 분리
   - RoomOptionTranslation 다국어 누락 추가

### 이슈 / 학습
- 대실 슬롯 설계를 "시작시간별 이용시간 슬롯"에서 "30분 블록 타임라인"으로 변경. 슬롯 겹침 문제 해결, 로우 수 예측 가능
- 숙박/대실 공존 시 시간대 충돌이 아닌 시간대 분리로 해결. 체크인 시간 기준으로 판단
- 대실 단축 이용: 운영 종료 임박 시 이용 가능 시간을 반환하여 고객 선택 유도

### 개발자 코멘트
1. 
