
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
