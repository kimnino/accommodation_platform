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

- [ ] 토큰 없이 Admin API 호출 → **403**
- [ ] 토큰 없이 Extranet API 호출 → **403**
- [ ] 토큰 없이 예약 API 호출 → **403**
- [ ] 토큰 없이 숙소 검색 API → **200** (공개)
- [ ] ADMIN 토큰 → Admin API → **200**
- [ ] ADMIN 토큰 → Extranet API → **403**
- [ ] PARTNER 토큰 → Extranet API → **200**
- [ ] PARTNER 토큰 → Admin API → **403**
- [ ] CUSTOMER 토큰 → 예약 API → **200**
- [ ] CUSTOMER 토큰 → Admin API → **403**
- [ ] `api-test.html` 접근 → **200**

---

## 3. 핵심 기능 검증 (API)

> `docs/plan/api-verification-checklist.md`의 curl 명령어를 실행하며 체크.
> 아래는 반드시 통과해야 하는 핵심만 추린 것.

### 고객 — 검색

- [ ] 전체 검색: 숙소 6개 나오는지 (HOTEL, RESORT, GUEST_HOUSE, MOTEL, PENSION, POOL_VILLA)
- [ ] 지역 필터 (서울): 서울 숙소만 나오는지
- [ ] 숙소 유형 필터 (PENSION): 펜션만 나오는지
- [ ] 가격 범위 필터: 범위 내 숙소만 나오는지
- [ ] 숙소 상세: 객실 목록 + 옵션 + 가격 + 잔여 재고 표시
- [ ] 재고 없는 날짜 검색: `lowest_price: null`, `has_available_room: false`

### 고객 — 예약

- [ ] 숙박 예약 생성 → status: PAYMENT_WAITING, hold_expired_at 설정
- [ ] 결제 확인 → status: CONFIRMED
- [ ] 예약 취소 → 재고 복구 확인
- [ ] 내 예약 목록 조회
- [ ] 멱등성: 같은 reservation_request_id로 재요청 → 에러
- [ ] 재고 소진 후 예약 시도 → INVENTORY_NOT_AVAILABLE (409)
- [ ] 연박 예약 (2박): 가격이 2일분 합산인지

### 파트너 — Extranet

- [ ] 내 숙소 목록 (partner_id별 필터)
- [ ] 숙소 등록 (다국어 포함) → status: PENDING
- [ ] 객실 등록
- [ ] 객실 옵션 등록 (번역 포함)
- [ ] 재고 설정 (날짜 범위)
- [ ] 재고 조회
- [ ] 요금 설정 (STAY/HOURLY)
- [ ] 요금 조회
- [ ] 숙소 수정 → 수정 요청 생성 (즉시 반영 아님)
- [ ] 파트너 예약 현황 조회
- [ ] 다른 파트너 숙소 접근 시도 → 거부되는지

### 관리자 — Admin

- [ ] 전체 숙소 목록 (파트너 구분 없이 전부)
- [ ] 숙소 상세
- [ ] 숙소 승인 (PENDING → ACTIVE)
- [ ] 숙소 정지 (ACTIVE → SUSPENDED)
- [ ] 숙소 폐쇄 (→ CLOSED)
- [ ] 잘못된 상태 전환 → 에러
- [ ] 수정 요청 목록 (pending)
- [ ] 수정 요청 승인 → 실제 데이터 변경 확인
- [ ] 수정 요청 거절 (사유 포함)
- [ ] 태그 그룹 CRUD + 활성/비활성
- [ ] 태그 CRUD + 활성/비활성
- [ ] 관리자 가격 조정

### 동시성

- [ ] 재고 1개에 5명 동시 예약 → 정확히 1명만 성공

### 에러 처리

- [ ] 필수 필드 누락 → VALIDATION_ERROR + field_errors
- [ ] 존재하지 않는 리소스 → NOT_FOUND
- [ ] 잘못된 날짜 형식 → 400

---

## 4. 샘플 데이터 확인

- [ ] 숙소 6개 유형별 1개씩 존재
- [ ] 호텔: 2객실 × 2옵션 (기본/조식, 기본/스파)
- [ ] 모텔: 2객실, 숙박+대실 가격 모두 있음
- [ ] 나머지 (리조트/게스트하우스/펜션/풀빌라): 1객실 × 1옵션
- [ ] 재고: 4/22~5/10 범위
- [ ] 요금: 평일/주말 구분
- [ ] 태그 그룹 8개 (유형별 + 공용 + 객실), 태그 27개
- [ ] 숙소-태그 매핑 되어있음

---

## 5. README.md

- [ ] 프로젝트 소개가 명확한지
- [ ] 아키텍처 다이어그램이 현재 구조와 일치하는지
- [ ] DomainServiceConfig 언급 제거 (삭제됨, @Service 직접 사용)
- [ ] Demo Pages 섹션 → API Test Tool로 교체
- [ ] Build & Run에 Docker 사전 조건 명시
- [ ] JWT 인증/인가 설명 추가
- [ ] Documentation 섹션의 링크가 실제 파일과 일치하는지
- [ ] 오타/깨진 링크 없는지

---

## 6. docs/ 정리

### 유지해야 할 문서

| 파일 | 용도 | 확인 |
|------|------|------|
| `conventions/code-conventions.md` | 코드 규칙 (CLAUDE.md에서 참조) | [ ] 내용 현재와 일치 |
| `design/multilingual.md` | 다국어 설계 | [ ] 내용 유효 |
| `design/search-performance-design.md` | 검색 성능 전략 | [ ] 내용 유효 |
| `design/supplier-sync-design.md` | 공급사 연동 설계 | [ ] 내용 유효 |
| `design/supplier-category-sample.sql` | 카테고리 매핑 예시 | [ ] 내용 유효 |
| `design/event-architecture.md` | 이벤트 기반 아키텍처 설계 | [ ] 내용 유효 |
| `plan/work-plan.md` | 작업계획서 | [ ] Phase 상태 최신화 |
| `plan/code-review-checklist.md` | 코드 리뷰 체크리스트 | [ ] FLOW 2~4 피드백 반영 체크 |
| `plan/api-verification-checklist.md` | API 검증 체크리스트 | [ ] curl 명령어 유효 |
| `plan/final-checklist.md` | 최종 점검 | [ ] Section 5 Demo UI → 삭제/수정 필요 |
| `plan/feedback/` | 피드백 이력 | [ ] 그대로 유지 (개발 과정 기록) |
| `devlog/` | 개발일지 | [ ] 0422 포함 5개 존재 |
| `research/` | 초기 리서치 | [ ] 그대로 유지 |
| `request/claude-request.md` | 초기 요청서 | [ ] 그대로 유지 |

### 삭제/수정 필요

- [ ] `plan/final-checklist.md` Section 5 (Demo UI) — 삭제된 HTML 참조 제거
- [ ] `plan/work-plan.md` Phase 9 미완료 항목 → 완료로 업데이트

---

## 7. 코드 품질

- [ ] `git status` clean (커밋 안 된 파일 없음)
- [ ] 불필요한 `System.out.println` 없는지 (`grep -rn "System.out" src/main/`)
- [ ] 불필요한 `TODO` 주석 확인 (`grep -rn "TODO" src/main/` — 의도적인 것만 남기기)
- [ ] 미사용 import 확인 (IDE 기준)
- [ ] application.yaml JWT secret이 dev용임을 주석으로 명시

---

## 8. Git 정리

- [ ] 불필요한 브랜치 삭제 (머지된 feature 브랜치)
- [ ] main 브랜치에 최신 코드 머지
- [ ] 커밋 히스토리가 깔끔한지 (의미 있는 커밋 메시지)
- [ ] `.gitignore`에 빠진 것 없는지 (build/, .gradle/, .idea/)

---

## 9. 실행 가이드 (다른 사람이 따라할 수 있는지)

- [ ] README 보고 clone → docker compose up → bootRun → api-test.html 접속까지 막힘 없이 가능한지
- [ ] Java 21 필요 명시
- [ ] Docker 필요 명시
- [ ] MySQL 포트 3307 명시
- [ ] 토큰 발급 → API 호출 방법 안내

---

## 체크 완료 후

모든 항목 체크 → main에 머지 → Git 주소 공유
