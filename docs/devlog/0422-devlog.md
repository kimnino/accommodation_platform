## 2026/04/22 - [보안 구현 + 코드 리뷰 피드백 반영 + Demo UI 정리]

### 수행 내용

1. Demo UI 전면 정리
   - 기존 개별 HTML 12개 삭제 (admin.html, admin-acc.html, admin-price.html, admin-tags.html, customer.html, partner.html, partner-acc.html, partner-hourly.html, partner-res.html, partner-rooms.html, partner-setup.html, partner-tags.html)
   - index.html 삭제 (아키텍처 소개 페이지)
   - `api-test.html` 1개로 통합 — 역할별 토큰 발급 + API 호출/응답 테스트 전용
   - `docs/checklist.md` 삭제 (final-checklist.md와 중복)

2. JWT 인증/인가 구현
   - `JwtTokenProvider` — HMAC-SHA512 서명, 24시간 만료, role/partnerId/memberId 클레임
   - `JwtAuthenticationFilter` — Bearer 토큰 추출 → SecurityContext 설정 + `HttpServletRequestWrapper`로 X-Partner-Id/X-Member-Id 헤더 자동 주입
   - `AuthController` — `POST /api/v1/auth/token` 토큰 발급 (데모/테스트용)
   - `SecurityConfig` — URL별 Role 기반 접근 제어
     - `/api/v1/auth/**`, `/api/v1/accommodations/**` → permitAll
     - `/api/v1/admin/**`, `/api/v1/supplier/**` → ADMIN
     - `/api/v1/extranet/**` → PARTNER
     - `/api/v1/reservations/**` → CUSTOMER
   - 보안 검증 완료: 11개 시나리오 전부 정상 (403/401/200)

3. 이벤트 기반 아키텍처 설계 문서
   - `docs/design/event-architecture.md` — 8개 이벤트 발행/구독 설계, 2단계 구현 전략 (Spring ApplicationEvent → Kafka), ASCII 흐름 다이어그램

4. 샘플 데이터 보강 (`data.sql` 전면 재작성)
   - 숙소 유형별 1개씩 6개 (HOTEL, RESORT, GUEST_HOUSE, MOTEL, PENSION, POOL_VILLA)
   - 호텔: 2객실(디럭스/스위트) × 2옵션(기본/조식, 기본/스파패키지)
   - 나머지: 1객실 × 1옵션 (모텔만 2객실 — 숙박+대실 가격)
   - 재고 4/22~5/10, 요금 평일/주말 구분
   - 태그 그룹 8개 (유형별 + 공용 + 객실), 태그 27개

5. 코드 리뷰 피드백 반영 (FLOW 2~4, 6건)
   - **FLOW 2-1**: `UpdateAccommodationRequest`에 `translations` 필드 추가
   - **FLOW 2-2**: `ExtranetUpdateAccommodationService`에서 JpaRepository 직접 참조 제거 → `PersistModificationRequestPort` + `LoadModificationRequestPort` + `ModificationRequestJpaAdapter` 분리
   - **FLOW 2-3**: `AdminApproveModificationUseCase`에서 조회(`listPending`) 분리 → `AdminGetModificationQuery` + `AdminGetModificationService` 생성, `ModificationRequestSummary` record 반환 (JPA 엔티티 노출 제거)
   - **FLOW 2-4**: `RejectModificationRequest` inner record → 별도 파일로 추출
   - **FLOW 4-1**: `RoomMapper` default 메서드 → MapStruct `@Mapping` + `@AfterMapping` 패턴으로 전환
   - **FLOW 4-2**: 객실 옵션 등록 시 번역 지원 → `RegisterRoomOptionRequest`에 translations 추가, `PersistRoomOptionTranslationPort` + `RoomOptionTranslationJpaAdapter` 생성

6. 테스트 인프라 수정
   - `TestSecurityConfig` 생성 — `@WebMvcTest`에서 Security 비활성화 + JWT Bean Mock
   - `application-test.yaml` — `sql.init.mode: never` 추가 (data.sql 충돌 방지), JWT secret 설정
   - Security 관련 Bean에 `@Profile("!test")` 적용
   - 전체 테스트 통과 확인 (BUILD SUCCESSFUL)

7. API 검증 체크리스트 작성
   - `docs/plan/api-verification-checklist.md` — curl 기반 47개 항목 (검색, 예약, 동시성, 보안, 에러처리)

### 이슈 / 학습

- `@WebMvcTest`에서 `@Profile`이 컴포넌트 스캔을 제어하지 못함. `@MockitoBean`으로 JWT Bean을 개별 테스트에서 명시적으로 Mock 처리해야 함
- `JwtAuthenticationFilter`를 `@MockitoBean`으로 등록하면 Filter Chain이 끊어져 컨트롤러까지 요청이 전달되지 않음. Filter는 Mock 대신 `@TestConfiguration`으로 Security를 별도 구성하는 게 안전
- `onclick` 속성에 JSON 객체를 직접 넣으면 큰따옴표가 HTML attribute를 깨뜨림. 전역 Map에 저장하고 키로 참조하는 패턴으로 해결

### 개발자 코멘트
