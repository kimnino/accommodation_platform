
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

### 개발자 코멘트
1. 
