# 최종 점검 체크리스트

> 작성: 2026-04-21 | 마무리 단계 전 확인 사항만 집약

---

## 1. 설계 결정 사항 (확정 필요)

- [x] **관리자 가격 직접 조정 정책** — `AdminAdjustPriceUseCase`가 파트너 판매가를 직접 수정하는 것이 적절한지 검토. 운영 정책상 관리자 개입 범위 확정 필요
- > 해당 프로젝트에서는 관리자는 숙소 파트너의 숙소등록/수정 에 승인해주는 역할만 하고, 숙소의 내용 관련해서 권한은 없도록 설계 ( 당연히 실제 서비스에서는 관리자는 모든 기능이 있어야함)
- > 결국 숙소파트너의 모든 기능을 관리자가 할 수 있어야 하는 것이므로 작업 범위가 넓다면 설계 정도만 추가해주고 실제 구현은 생략.
- > **설계 확정**: `AdminAdjustPriceUseCase`는 인터페이스 + Service 구현체 존재 (REST Docs 테스트 포함). 이 프로젝트에서 관리자 역할은 숙소 승인으로 한정. 가격 조정 포함 파트너 기능 전체를 관리자가 수행할 수 있어야 하나, 구현 범위 초과로 설계 수준에서 마무리.
- [x] **AccommodationType 외부 연동 처리** — `EXTERNAL` 단일 타입 추가 완료. 공급사 구분은 `supplier_accommodation_mapping`으로 처리.
- > `AccommodationType.EXTERNAL` 추가됨. A_HOTEL/B_HOTEL 방식 지양 — 공급사 식별은 매핑 테이블 책임.
- [x] **외부 연동 ON/OFF 주체** — `supplierManaged` 플래그 (이미 `AccommodationJpaEntity`에 존재). 관리자 전용으로 확정.
- > 설계 문서: `docs/design/supplier-sync-design.md` 참조. 관리자 API(`PATCH /admin/accommodations/{id}/supplier-managed`) 설계 완료, 구현 생략.

---

## 2. 미구현 / 스텁 상태 항목

- [x] **SyncSupplierInventoryService** — 가격/재고 동기화 기본 구현 완료. Virtual Thread + 전체 공급사 병렬 동기화 설계 문서 작성.
- > 설계 문서: `docs/design/supplier-sync-design.md`
- [x] **S3ImageUploader** — 구현 생략. 설계 내용 코드 주석으로 상세 작성 완료.
- > S3Client 업로드 흐름, CloudFront OAC 연동, 용량/형식 제한 주석 추가됨.
- [x] **HoldExpirationScheduler 재고 복구 검증** — 10분 TTL 만료 시 `inventory.remaining` 복구 동작 통합 테스트 작성 완료.
- > 스케줄러가 1분 주기로 `PAYMENT_WAITING` 예약을 조회해서 `holdExpiredAt`이 지난 것을 자동 취소하고 재고를 복구함. 비관적 락(`findWithLock`) 적용으로 동시성 안전.
- > 테스트: `HoldExpirationSchedulerTest` — 만료 케이스(2박 재고 복구 확인), 미만료 케이스(재고 유지 확인) 2개 작성.
- > 동시성 시나리오: 재고 1개 남았을 때 예약 생성과 만료 복구가 동시에 접근하면, 둘 다 `PESSIMISTIC_WRITE` 락을 사용하므로 DB가 직렬화 처리. 먼저 락을 획득한 쪽이 완료된 후 다음 쪽이 실행됨 → 데이터 정합성 보장.
- [x] **검색 성능 전략** — 설계 문서 작성 완료.
- > 설계 문서: `docs/design/search-performance-design.md` (스냅샷 테이블, Redis 캐시, CQRS 확장, 구현 우선순위 포함)
- [x] **AccommodationSummary.lowestPrice** — Inventory가 없을 때 null 처리 동작 확인 완료.
- > inventory가 없다는건 해당 일자나 시간에 판매를 하지 않는다고 생각해야해. 말그래도 아이템이 없다.
- > **확인 결과**: `AccommodationSearchJpaAdapter.fetchLowestPrices()`가 `room_price` 기준으로 MIN 집계하므로, 가격 미설정 숙소는 map에 키가 없음 → `lowestPrices.get()` = null → `AccommodationSummary.lowestPrice = null`. 가격 필터(minPrice/maxPrice) 시 EXISTS 서브쿼리에서 자연히 제외됨. 설계 의도대로 동작.

---

## 3. 코드 검토 (핵심 플로우)

### 공통 인프라

- [x] `GlobalExceptionHandler` — 404/400/500 케이스 모두 `ApiResponse` 포맷으로 응답 확인 ✅
- [x] `BaseJpaEntity.restoreTimestamps` — `@PreUpdate`는 `updatedAt`만 덮어씀. `createdAt`은 보존됨. `updatedAt`을 항상 저장 시각으로 갱신하는 것은 의도된 동작으로 확인.
- [x] `DomainServiceConfig` 유지 → `PriceDomainService`, `InventoryDomainService`는 순수 Java 클래스로 `DomainServiceConfig`에서 `@Bean` 등록 ✅

### 숙소 등록 / 수정 (파트너)

- [x] `ExtranetRegisterAccommodationService` — `persistTranslationPort.saveAll()` + `saveSupportedLocales()` 모두 호출됨 ✅
- [x] `ExtranetUpdateAccommodationService` — `JacksonConfig`에 `NON_NULL` 설정 없음 → null 필드 그대로 직렬화됨 ✅
- [x] `AdminApproveModificationService` — **버그 수정**: `latitude`/`longitude` null 체크 누락 → 기존 좌표 보존 로직 추가 완료. 나머지 필드는 정상.

### 예약 (고객)

- [x] `CustomerCreateReservationService` — 날짜별 `findWithLock()` (PESSIMISTIC_WRITE) 후 `decrease(1)` 경로 확인 ✅
- [x] `Reservation.reconstruct()` — `r.holdExpiredAt = holdExpiredAt` 명시적 복원 ✅
- [x] `hourlyUsageMinutes` — 대실 예약 생성 시 `end_time - start_time` 으로 계산 저장됨 ✅

### 태그

- [x] `TagGroup.updateInfo()` — `accommodationType` null 재설정 허용 로직 수정됨 ✅
- [x] `AdminTagController` REST Docs — `supplier_id` 필드 포함 ✅

### 매퍼 (MapStruct)

- [x] 빌드 시 MapStruct 생성 코드 컴파일 오류 없는지 확인 (`./gradlew build`) ✅
- [x] `AccommodationMapper` — `@Mapping(target="images", ignore=true)` + `@AfterMapping` 이미지/타임스탬프 복원 정상 ✅
- [x] `TagMapper` — `@Mapping(source="active", target="isActive")` TagGroup/Tag 양쪽 적용 ✅

### 추가 버그 수정 (코드 검토 중 발견)

- [x] `HoldExpirationScheduler` — HOURLY 예약 만료 시 TimeSlotInventory 복구 로직 누락 → `restoreHourlyInventory()` 추가 완료. 점유 슬롯(OCCUPIED) + 버퍼 슬롯(BLOCKED) 모두 `release()` 처리.

---

## 4. 테스트 현황

| 테스트 | 상태 |
|--------|------|
| ExtranetAccommodationController REST Docs | ✅ |
| AdminTagController REST Docs | ✅ |
| AdminPriceController REST Docs | ✅ |
| CustomerReservationController REST Docs | ✅ |
| PriceDomainService 단위 테스트 | ✅ |
| CustomerSearchAccommodationService 단위 테스트 | ✅ |
| InventoryJpaAdapter 통합 테스트 (비관적 락) | ✅ |
| 동시성 테스트 (CountDownLatch) | ✅ |
| HoldExpirationScheduler 통합 테스트 | ✅ |

---

## 5. 빌드 & 정합성

- [x] `./gradlew test` — 전체 빌드 + 테스트 통과 (BUILD SUCCESSFUL in 1m 10s) ✅
- [x] Spring REST Docs 스니펫 생성 확인 (`build/generated-snippets/`) ✅
- [x] Testcontainers MySQL 통합 테스트 통과 ✅
- [x] `DomainServiceConfig.java` 유지 — `PriceDomainService`, `InventoryDomainService` `@Bean` 등록 정상 ✅
