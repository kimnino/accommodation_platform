## 2026/04/21 - [Phase 9 최종 점검 & 코드 리뷰]

### 수행 내용

1. 체크리스트 문서 통합
   - `checklist.md`, `code-review-checklist.md`, `todo.md` 내용을 `final-checklist.md` 하나로 통합
   - `todo.md` 삭제 (전 항목 완료 상태)
   - 이후 점검은 `final-checklist.md` 단일 파일로 관리

2. 설계 결정 확정 및 문서화
   - `AccommodationType.EXTERNAL` 추가 — 외부 공급사 숙소를 단일 타입으로 구분. 공급사 식별은 `supplier_accommodation_mapping` 책임
   - `supplierManaged` 플래그 — 이미 `AccommodationJpaEntity`에 존재 확인. 관리자 전용으로 확정
   - 관리자 가격 조정 정책 — 이 프로젝트에서 관리자 역할은 승인 한정. `AdminAdjustPriceUseCase`는 인터페이스 + Service 구현체 존재, 설계 수준에서 마무리
   - `AccommodationSummary.lowestPrice` null 처리 — 가격 없음 = 해당 날짜 판매 안 함. null 그대로 반환이 정상 동작임을 코드로 확인

3. 설계 문서 작성
   - `docs/design/supplier-sync-design.md` — Supplier 연동 전체 구조, Virtual Thread 병렬 동기화 설계, 미구현 항목 목록
   - `docs/design/search-performance-design.md` — 스냅샷 테이블, Redis 캐시, CQRS 확장 전략, 구현 우선순위

4. 버그 수정 2건
   - `AdminApproveModificationService.approve()` — `name`, `fullAddress` 등에는 null fallback이 있는데 `latitude`/`longitude`만 빠져 있었음. 파트너가 좌표 없이 수정 요청을 보내면 승인 시 기존 좌표가 null로 덮어쓰여지는 버그. 좌표 보존 로직 추가
   - `HoldExpirationScheduler` — HOURLY 예약 만료 시 `TimeSlotInventory` 복구 로직이 없었음. `ReservationType.STAY`만 처리하고 HOURLY는 누락. `restoreHourlyInventory()` 추가 — startTime ~ endTime+버퍼 범위 슬롯을 `release()` 처리

5. 테스트 추가
   - `HoldExpirationSchedulerTest` (Testcontainers 통합 테스트)
   - 만료된 홀드 예약 → CANCELLED + 2박 날짜별 재고 각각 복구 확인
   - 미만료 홀드 예약 → PAYMENT_WAITING 유지 + 재고 불변 확인

6. 코드 검토 (Section 3 전체)
   - `GlobalExceptionHandler` — 404/400/500 모두 `ApiResponse` 포맷 정상
   - `BaseJpaEntity.restoreTimestamps` — `@PreUpdate`가 `updatedAt = now()` 덮어씀. 저장 시각이 갱신 시각이 되는 것이 맞으므로 정상 동작
   - `ExtranetRegisterAccommodationService` — 번역/로케일 저장 모두 포함 확인
   - `ExtranetUpdateAccommodationService` — `JacksonConfig`에 `NON_NULL` 설정 없어 null 필드도 JSON에 포함됨. 의도대로 동작
   - `CustomerCreateReservationService` — 날짜별 `findWithLock()` (PESSIMISTIC_WRITE) + `decrease(1)` 경로 정상
   - `Reservation.reconstruct()` — `holdExpiredAt` 명시적 복원 확인
   - `AccommodationMapper` / `TagMapper` — MapStruct `@AfterMapping`, `images ignore`, `isActive` 매핑 모두 정상

7. 빌드 & 테스트 실행
   - `./gradlew test` → BUILD SUCCESSFUL (1분 10초)
   - 전체 테스트 통과 (Testcontainers MySQL 포함)

### 이슈 / 학습
- LSP(IDE 언어 서버)가 Lombok `@Getter`/`@RequiredArgsConstructor`로 생성된 메서드를 인식 못 해 false positive 오류를 다수 표시. 빌드는 정상이고 실제 문제가 아님. Lombok은 컴파일 타임에 처리되므로 LSP 단에서 annotation processor 결과를 못 읽는 것
- `restoreTimestamps` 메서드가 도메인 → JPA 변환 시 기존 타임스탬프를 세팅하는데, `@PreUpdate`가 `updatedAt`을 덮어씀. 처음엔 버그처럼 보였지만 저장 시 `updatedAt = now()`가 맞으므로 정상. `createdAt`은 `@PreUpdate`가 건드리지 않아 보존됨

### 개발자 코멘트
- 코드 검토를 직접 해보니 전체 플로우는 잘 잡혀 있는데 세부 null 처리에서 놓친 게 있었음. `latitude`/`longitude`처럼 같은 종류의 필드인데 일부만 처리된 경우는 코드 리뷰 단계에서 주의 깊게 봐야 함
- HoldExpirationScheduler HOURLY 누락은 STAY 먼저 구현하고 HOURLY를 나중에 추가하면서 스케줄러를 같이 업데이트하지 않은 것. 새 케이스 추가 시 연관된 모든 처리 경로를 같이 확인해야 함
