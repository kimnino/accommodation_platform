# OTA 숙박 플랫폼

OTA(Online Travel Agency) 숙박 플랫폼 백엔드 시스템. 헥사고날 아키텍처 기반으로 숙소 등록, 재고/가격 관리, 검색, 예약(동시성 처리), 외부 공급사 연동까지 구현한 학습 프로젝트.

## Tech Stack

- **Java 21** / **Spring Boot 4.0.5** / Gradle
- **MySQL 8.0** (Main DB)
- **QueryDSL** (동적 검색 쿼리)
- **Testcontainers** (통합 테스트)
- **Spring REST Docs** (테스트 기반 API 문서)

## 아키텍처

**헥사고날 아키텍처 (Ports & Adapters)** + **채널별 최상위 분리**

```
core/         → 공유 도메인 (숙소, 객실, 재고, 가격, 예약, 태그, 공급사)
extranet/     → 숙소 파트너 채널 API
admin/        → 관리자 채널 API
customer/     → 고객 채널 API
```

## 빠른 시작

### 방법 1: Docker 사용 (권장)

```bash
# 1. 프로젝트 클론
git clone https://github.com/kimnino/accommodation_platform.git
cd accommodation_platform

# 2. MySQL 컨테이너 실행
docker compose up -d

# 3. 앱 실행 (샘플 데이터 자동 로드)
./gradlew bootRun --args='--spring.profiles.active=local'

# 4. 브라우저에서 접속
open http://localhost:8080
```

### 방법 2: 로컬 MySQL 사용

```bash
# 1. MySQL에서 DB 생성
mysql -u root -p -e "CREATE DATABASE accommodation_platform;"

# 2. application.yaml의 datasource 설정 확인 (기본: root/root)

# 3. 앱 실행
./gradlew bootRun --args='--spring.profiles.active=local'

# 4. 브라우저에서 접속
open http://localhost:8080
```

### 종료

```bash
# 앱 종료: Ctrl+C
# Docker 컨테이너 종료
docker compose down
```

## Demo UI 사용법

http://localhost:8080 접속 시 3개 채널 화면을 체험할 수 있습니다.

### 1. 숙소 파트너 (Extranet)

| 기능 | 설명 |
|------|------|
| 내 숙소 목록 | 파트너 ID별 등록된 숙소 목록 조회 |
| 숙소 등록 | 숙소명, 유형, 주소, 체크인/아웃 시간 입력 → PENDING 상태로 등록 |
| 객실 관리 | 숙소 선택 후 객실 추가 (객실명, 유형, 인원) |
| 재고 설정 | 객실 옵션 선택 → 날짜 범위 + 수량 설정 |
| 가격 설정 | 객실 옵션 선택 → 숙박/대실 가격 설정 |

> 파트너 ID를 변경하면 해당 파트너의 숙소만 조회됩니다.

### 2. 관리자 (Admin)

| 기능 | 설명 |
|------|------|
| 숙소 목록 | 전체 숙소 상태 확인 |
| 숙소 승인 | PENDING → ACTIVE 전환 (고객 검색에 노출) |
| 숙소 정지/폐쇄 | ACTIVE → SUSPENDED / CLOSED |
| 태그 관리 | 태그 그룹 생성, 태그 추가 |

### 3. 고객 (Customer)

| 기능 | 설명 |
|------|------|
| 숙소 검색 | 지역, 날짜, 인원, 숙소유형으로 검색 |
| 숙소 상세 | 객실별 옵션, 가격, 잔여 재고 확인 |
| 예약 | 투숙객 정보 입력 → 예약 생성 (10분 내 결제 필요) |
| 내 예약 | 예약 목록 조회, 예약 취소 |

> 회원 ID를 변경하면 해당 회원의 예약만 조회됩니다.

### 샘플 데이터

`local` 프로필로 실행하면 자동으로 샘플 데이터가 로드됩니다:

| 숙소 | 유형 | 파트너 |
|------|------|--------|
| 서울 그랜드 호텔 | HOTEL | 파트너 1 |
| 해운대 리조트 | RESORT | 파트너 1 |
| 홍대 게스트하우스 | GUEST_HOUSE | 파트너 2 |
| 역삼 모텔 | MOTEL | 파트너 2 |

- 재고: 2026-04-25 ~ 2026-04-30 (옵션별 1~5개)
- 가격: 25,000원 ~ 250,000원 (숙소별 차등)
- 태그: 수영장, 사우나, 피트니스, 룸서비스, 발렛파킹

## 빌드 & 테스트

```bash
./gradlew build        # 빌드 + 테스트
./gradlew test         # 테스트만
./gradlew asciidoctor  # REST Docs 문서 생성 (build/docs/asciidoc/index.html)
```

## API 문서

REST Docs 기반 API 문서는 빌드 후 `build/docs/asciidoc/index.html`에서 확인:

```bash
./gradlew asciidoctor && open build/docs/asciidoc/index.html
```

## 프로젝트 문서

| 문서 | 위치 |
|------|------|
| 작업계획서 | `docs/plan/work-plan.md` |
| 코드 컨벤션 | `docs/conventions/code-conventions.md` |
| 개발일지 | `docs/devlog/` |
| 피드백 기록 | `docs/plan/feedback/` |
