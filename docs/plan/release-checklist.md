# 최종 릴리스 체크리스트

> 다른 사람에게 Git 주소를 보내기 전, 아래 항목을 전부 체크한다.
> 작성: 2026-04-22

---

## 1. 빌드 & 테스트

- [ ] `./gradlew clean build` 성공 (테스트 포함)
- [ ] 테스트 전체 통과 확인 (`build/reports/tests/test/index.html` 열어서 빨간불 없는지)
- [ ] `docker compose up -d` → `./gradlew bootRun` → 서버 정상 기동 (8080)
- [ ] 서버 시작 로그에 에러/WARN 없는지 확인 (Hibernate DDL 제외)

---

## 2. 보안 (JWT 인증/인가)

> 토큰 발급: `curl -s -X POST -H 'Content-Type: application/json' http://localhost:8080/api/v1/auth/token -d '{"role":"ADMIN"}'`

**인증 없음 (401)**
- [ ] 토큰 없이 Admin API 호출 → **401**
- [ ] 토큰 없이 Extranet API 호출 → **401**
- [ ] 토큰 없이 예약 API 호출 → **401**

**공개 API (200)**
- [ ] 토큰 없이 숙소 검색 API → **200**
- [ ] 토큰 없이 지역 목록 API → **200**
- [ ] 토큰 없이 태그 목록 API → **200**

**역할별 접근 제어 (403)**
- [ ] ADMIN 토큰 → Admin API → **200**
- [ ] ADMIN 토큰 → Extranet API → **403**
- [ ] PARTNER 토큰 → Extranet API → **200**
- [ ] PARTNER 토큰 → Admin API → **403**
- [ ] CUSTOMER 토큰 → 예약 API → **200**
- [ ] CUSTOMER 토큰 → Admin API → **403**

**기타**
- [ ] `api-test.html` 접근 → **200**

---

## 3. 핵심 기능 검증 (시나리오)

> `src/main/resources/static/api-test.html` 의 시나리오 버튼으로 실행하거나 curl로 직접 검증.

### S-A1: 숙소 승인 플로우

- [ ] PENDING 숙소 목록 조회 → 결과 있음
- [ ] 첫 번째 PENDING 숙소 승인 → status: `ACTIVE`

### S-P1: 전체 파트너 설정 플로우

- [ ] 숙소 등록 → status: `PENDING`
- [ ] 객실 등록
- [ ] 객실 옵션 등록
- [ ] 지역 설정
- [ ] 재고 설정 (날짜 범위)
- [ ] 요금 설정 (STAY)

### S-C1: 숙박 예약 전체 플로우

- [ ] 숙소 검색 → 결과 있음
- [ ] 숙소 상세 조회 → 객실/옵션/가격/재고 포함
- [ ] 숙박 예약 생성 → status: `PAYMENT_WAITING`, `hold_expired_at` 설정
- [ ] 결제 확인 → status: `CONFIRMED`

### S-C2: 대실 예약 전체 플로우

- [ ] 숙소 상세 조회
- [ ] 대실 예약 생성 → status: `PAYMENT_WAITING`
- [ ] 결제 확인 → status: `CONFIRMED`

### S-C3: 재고 소진 후 예약 차단

- [ ] 첫 번째 예약 → **201 성공**
- [ ] 두 번째 예약 (동일 날짜/옵션) → **409 INVENTORY_NOT_AVAILABLE**

### S-C4: 예약 취소 플로우

- [ ] 예약 생성
- [ ] 예약 취소 → **200** (재고 복구 확인)

### 추가 검증

- [ ] 멱등성: 같은 `reservation_request_id` 재요청 → 중복 처리 없이 동일 응답
- [ ] 연박 예약 (2박): 가격이 2일분 합산인지
- [ ] 재고 없는 날짜 검색: `remaining_quantity: 0` 또는 조회 불가 확인

---

## 4. 샘플 데이터 확인

- [ ] 숙소 유형별 샘플 존재 (HOTEL, MOTEL, PENSION, RESORT, GUEST_HOUSE 등)
- [ ] 재고: 현재 날짜 기준 충분한 범위 설정 (`2026-04-22 ~ 2026-05-10` 등)
- [ ] 요금: 숙박/대실 가격 모두 있음
- [ ] 태그 그룹 및 태그 데이터 존재
- [ ] PENDING 숙소 최소 1개 이상 (S-A1 시나리오용)

---

## 5. README.md

- [ ] 프로젝트 소개가 명확한지
- [ ] 아키텍처 다이어그램이 현재 구조와 일치하는지 (`DomainServiceConfig` 언급 제거 — 삭제됨, `@Service` 직접 사용)
- [ ] Build & Run에 Docker 사전 조건 명시 (Java 21, Docker, MySQL 포트 3307)
- [ ] JWT 인증/인가 설명 있는지 (`docs/design/security.md` 링크 추가)
- [ ] API Test Tool 안내 (`/api-test.html`) 있는지
- [ ] Documentation 섹션의 링크가 실제 파일과 일치하는지
- [ ] 오타/깨진 링크 없는지

---

## 6. docs/ 정리

### 유지할 문서

| 파일 | 용도 |
|------|------|
| `conventions/code-conventions.md` | 코드 규칙 |
| `design/database.md` | DB 테이블 레퍼런스 |
| `design/erd.png` | ERD 이미지 |
| `design/schema.sql` | 스키마 DDL |
| `design/security.md` | JWT/RBAC 인증 설계 |
| `design/multilingual.md` | 다국어 설계 |
| `design/search-performance-design.md` | 검색 성능 전략 |
| `design/supplier-sync-design.md` | 공급사 연동 설계 |
| `design/event-architecture.md` | 이벤트 기반 아키텍처 설계 |
| `design/redis-cache-design.md` | Redis 캐시 설계 |
| `src/docs/asciidoc/` | API 문서 (adoc) |

### 검토 후 정리

- [ ] `docs/plan/feedback/` — 개발 과정 피드백 이력. 공유 대상에 맞게 유지 또는 삭제
- [ ] `docs/plan/work-plan.md` — 내부 작업 계획. 공유 시 필요 여부 판단
- [ ] `docs/devlog/` — 개발 일지. 공유 시 필요 여부 판단
- [ ] `docs/request/claude-request.md` — 초기 요청서. 공유 시 필요 여부 판단

---

## 7. 코드 품질

- [ ] `git status` clean (커밋 안 된 파일 없음)
- [ ] 불필요한 `System.out.println` 없는지 (`grep -rn "System.out" src/main/` → 현재 0건 확인됨)
- [ ] `TODO` 주석 확인 (`grep -rn "TODO" src/main/` → 현재 20건, 전부 미구현 도메인 stub 및 S3 — 의도적 유지)
- [ ] `application.yaml` JWT secret이 dev용임을 확인 (현재 키명에 `for-development-only` 포함)

---

## 8. Git 정리

- [ ] 불필요한 브랜치 삭제 (머지된 feature 브랜치)
- [ ] main 브랜치에 최신 코드 머지
- [ ] 커밋 히스토리가 깔끔한지 (의미 있는 커밋 메시지)
- [ ] `.gitignore`에 빠진 것 없는지 (`build/`, `.gradle/`, `.idea/`, `.omc/` 확인)

---

## 9. 실행 가이드 (다른 사람이 따라할 수 있는지)

- [ ] README 보고 clone → `docker compose up -d` → `./gradlew bootRun` → `http://localhost:8080/api-test.html` 접속까지 막힘 없이 가능한지
- [ ] Java 21 필요 명시
- [ ] Docker 필요 명시 (MySQL 포트 3307)
- [ ] 토큰 발급 방법 안내: `POST /api/v1/auth/token` + `{"role":"ADMIN"}` / `{"role":"PARTNER","partner_id":1}` / `{"role":"CUSTOMER","member_id":1}`

---

## 체크 완료 후

모든 항목 체크 → main에 머지 → Git 주소 공유
