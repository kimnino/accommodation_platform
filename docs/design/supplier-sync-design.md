# 외부 공급사 연동 설계

> 작성: 2026-04-21

---

## 1. 개요

외부 공급사(Expedia, Booking.com 등)의 숙소·가격·재고 데이터를 플랫폼 내부 도메인으로 정규화·동기화하는 구조.
실제 업체 API는 연동 불가하므로 설계 + 기본 구현(Mock SupplierClient)으로 대체.

---

## 2. 공급사 연동 숙소 타입

```
AccommodationType.EXTERNAL
```

- 연동 출처(Expedia vs Booking.com)는 `supplier_accommodation_mapping.supplier_id`로 구분
- 숙소 유형에 A_HOTEL, B_HOTEL 방식은 사용하지 않음 → 관심사 분리
- 공급사 연동 숙소는 `AccommodationJpaEntity.supplierManaged = true`

---

## 3. 주요 테이블

```
supplier                          # 공급사 마스터
  id, code (MINHYUK_HOUSE 등), name, is_active

supplier_accommodation_mapping    # 공급사 숙소 ↔ 내부 숙소 매핑
  id, supplier_id, external_accommodation_id, internal_accommodation_id, sync_status

supplier_room_mapping             # 공급사 객실 ↔ 내부 Room 매핑
  id, supplier_id, external_room_id, internal_room_id

supplier_room_option_mapping      # 공급사 객실 옵션 ↔ 내부 RoomOption 매핑 (3단 매핑)
  id, supplier_id, external_room_option_id, internal_room_option_id

supplier_category_mapping         # 공급사 카테고리 → 내부 태그 매핑
  id, supplier_id, external_category, internal_tag_id, mapping_status (MAPPED/PENDING/IGNORED)
```

---

## 4. 동기화 흐름 (`SyncSupplierInventoryService`)

```
POST /api/v1/supplier/sync?supplierCode=MINHYUK_HOUSE&startDate=...&endDate=...
  ↓
SyncSupplierInventoryService.syncPricesAndInventory()
  ↓
1. SupplierClient.fetchPrices(externalAccommodationId, startDate, endDate)
   → List<CanonicalPrice> (정규화된 가격/재고 모델)
  ↓
2. externalRoomId → roomOptionId 해결 (resolveRoomOptionId)
   - 3단 매핑(supplier_room_option_mapping) 우선 확인
   - 없으면 2단 매핑(supplier_room_mapping) → 첫 번째 옵션 자동 연결
  ↓
3. syncPrice(): 날짜별 RoomPrice upsert
4. syncInventory(): 날짜별 Inventory upsert
```

---

## 5. Virtual Thread 적용 포인트

공급사가 여러 개일 때 각 공급사를 병렬로 동기화:

```java
// SyncAllSuppliersService (미구현 — 설계만)
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<SyncResult>> futures = activeSuppliers.stream()
        .map(supplier -> executor.submit(() ->
            syncService.syncPricesAndInventory(supplier.getCode(), startDate, endDate)))
        .toList();
    // 결과 수집 및 실패 로깅
}
```

- Virtual Thread는 I/O 블로킹(외부 API 호출)에 적합
- 공급사별 동기화 실패가 다른 공급사에 영향을 주지 않도록 격리

---

## 6. SupplierClient 인터페이스

```java
public interface SupplierClient {
    String getSupplierCode();
    List<CanonicalPrice> fetchPrices(String externalAccommodationId, LocalDate start, LocalDate end);
    List<CanonicalAccommodation> fetchAccommodations(); // 숙소 목록 동기화 (미구현)
}
```

현재 구현체: `MinhyukHouseSupplierAdapter` (Mock 응답 반환)

---

## 7. supplierManaged 플래그 관리

- `AccommodationJpaEntity.supplierManaged`: 공급사 연동 숙소 여부
- **ON/OFF 권한: 관리자 전용** (파트너가 임의 변경 불가)
- 관리자 API: `PATCH /api/v1/admin/accommodations/{id}/supplier-managed`
  - body: `{ "supplier_managed": true/false }`
  - 설계만 존재, 구현은 `AdminManageAccommodationUseCase`에 메서드 추가 필요

---

## 8. 초기 숙소 등록 흐름 (신규 공급사 연동 시)

```
1. 관리자: 공급사 마스터 등록 (POST /api/v1/admin/suppliers)
2. 공급사에서 숙소 목록 fetch (SupplierClient.fetchAccommodations)
3. CanonicalAccommodation → 내부 Accommodation 생성 (type = EXTERNAL, supplierManaged = true)
4. supplier_accommodation_mapping 레코드 생성
5. 객실/옵션도 동일 흐름
6. 이후 주기적 동기화: syncPricesAndInventory (가격/재고만 업데이트)
```

> 현재 3~6 단계는 미구현. SyncSupplierInventoryService는 이미 매핑된 숙소의 가격/재고 동기화만 담당.

---

## 9. 미구현 항목

| 항목 | 설명 |
|------|------|
| 신규 숙소 자동 등록 | fetchAccommodations() → 내부 숙소 생성 |
| 카테고리 → 태그 자동 매핑 | supplier_category_mapping 활용 |
| 동기화 이력 테이블 | sync_log: 성공/실패/건수 기록 |
| 관리자 supplierManaged 토글 API | PATCH /api/v1/admin/accommodations/{id}/supplier-managed |
| SyncAllSuppliersService | Virtual Thread로 전체 공급사 병렬 동기화 |
