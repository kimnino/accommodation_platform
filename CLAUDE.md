# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

숙소 플랫폼을 주제로 한 개인 학습용 Spring Boot 프로젝트. 단독 작업자가 `main` 브랜치에 직접 커밋/푸시하는 단순 워크플로우.

## 기술 스택

- **Java 21** (Gradle toolchain으로 고정)
- **Spring Boot 4.0.5** — `spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`
- **MySQL** (`mysql-connector-j`, runtime)
- **Lombok** (annotation processor 포함)
- **Spring REST Docs** — 테스트에서 생성된 스니펫(`build/generated-snippets`)을 Asciidoctor로 문서화

## 주요 명령어

```bash
# 애플리케이션 실행
./gradlew bootRun

# 전체 빌드 (컴파일 + 테스트 + JAR 패키징)
./gradlew build

# 테스트만 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests com.accommodation.platform.PlatformApplicationTests

# 단일 테스트 메서드 실행
./gradlew test --tests "com.accommodation.platform.SomeTest.methodName"

# REST Docs Asciidoctor 렌더링 (test 후 실행됨)
./gradlew asciidoctor

# 클린 빌드
./gradlew clean build
```

## 아키텍처 메모

- **핵사고날아키텍쳐**: 핵사고날 아키택쳐 구조로 진행
- **베이스 패키지**: `com.accommodation.platform` — 엔트리포인트는 `PlatformApplication`. 새 모듈/도메인을 추가할 때는 이 아래에 `domain`, `controller`, `service`, `repository` 등 레이어 혹은 기능별 하위 패키지로 분리.
- **설정 파일**: `src/main/resources/application.yaml` — 현재는 애플리케이션 이름만 선언. DB 연결/프로파일별 설정은 이 파일 또는 `application-{profile}.yaml`에 추가.
- **REST Docs 파이프라인**: `test` 태스크가 `build/generated-snippets`를 출력하고 `asciidoctor` 태스크가 이를 입력으로 받음 (`build.gradle` 참고). API 문서를 작성할 때는 MockMvc 테스트에서 스니펫을 생성한 뒤 `src/main/asciidoc/`에 `.adoc` 문서를 두는 구조를 따른다.
- **민감 설정 분리**: `application-secret.yml`, `*.local.properties`, `.env*`는 gitignore 대상. 시크릿은 이런 파일이나 환경변수로만 다룬다.

## 커밋 규칙
**사용자**의 요청이 있기 전까지는 절대 커밋을 하지 않는다.
모든 커밋 메시지는 **Conventional Commits 접두어**를 붙이고 **한글**로 작성한다:

- `feat:` 새 기능/엔티티/API/화면 추가
- `fix:` 버그 수정
- `refactor:` 동작 변경 없는 구조 개선
- `style:` 포맷/세미콜론/공백
- `docs:` 문서/주석 변경
- `test:` 테스트 추가/수정
- `chore:` 빌드/설정/gitignore/의존성 등
- `perf:` 성능 개선

예: `feat: 회원 엔티티 추가`, `fix: 숙소 검색 페이징 오류 수정`
커밋/푸시는 `/commit-push` 슬래시 커맨드로 수행 — 정의는 `.claude/skills/commit-push/SKILL.md`에 있으며, 변경 시 이 스킬도 함께 수정한다.

## Gradle 래퍼

`gradlew`, `gradlew.bat`, `gradle/wrapper/`는 저장소에 포함되어 있음. 로컬에 Gradle을 설치하지 않고 래퍼로 모든 빌드를 수행한다.
