# 코드 리뷰 체크리스트 (프로세스 흐름 기준)

> 각 플로우를 Controller → UseCase/Port → Service → Domain → Persistence 순서로 따라가며 리뷰
> 파일 경로: `src/main/java/com/accommodation/platform/` 기준

---

## FLOW 1. 숙소 등록 (파트너)

> `POST /api/v1/extranet/accommodations`

| 순서 | 파일 | 확인 포인트 | 완료    |
|------|------|-------------|-------|
| 1 | `extranet/accommodation/adapter/in/web/RegisterAccommodationRequest.java` | 필드 검증(`@Valid`), snake_case 매핑 | [ V ] |
| 2 | `extranet/accommodation/adapter/in/web/ExtranetAccommodationController.java` | 엔드포인트 정의, Command 변환 | [ V ] |
| 3 | `extranet/accommodation/application/port/in/ExtranetRegisterAccommodationUseCase.java` | Command record 정의 | [ V ] |
| 4 | `extranet/accommodation/application/service/ExtranetRegisterAccommodationService.java` | 비즈니스 로직, 트랜잭션 경계 | [ V ] |
| 5 | `core/accommodation/domain/model/Accommodation.java` | 불변식, 상태 초기화, builder | [ V ] |
| 6 | `core/accommodation/domain/enums/AccommodationStatus.java` | 상태 정의 | [ V ] |
| 7 | `core/accommodation/domain/enums/AccommodationType.java` | 유형 정의 | [ V ] |
| 8 | `core/accommodation/application/port/out/PersistAccommodationPort.java` | 저장 포트 | [ V ] |
| 9 | `core/accommodation/application/port/out/PersistAccommodationTranslationPort.java` | 번역 저장 포트 | [ V ] |
| 10 | `core/accommodation/adapter/out/persistence/AccommodationMapper.java` | 도메인 ↔ JPA Entity 변환, restoreTimestamps | [ V ] |
| 11 | `core/accommodation/adapter/out/persistence/AccommodationJpaEntity.java` | 테이블 정의, 필드 주석 | [ V ] |
| 12 | `core/accommodation/adapter/out/persistence/AccommodationTranslationJpaEntity.java` | 번역 테이블 정의 | [ V ] |
| 13 | `core/accommodation/adapter/out/persistence/AccommodationJpaAdapter.java` | 포트 구현체 | [ V ] |
| 14 | `core/accommodation/adapter/out/persistence/AccommodationTranslationJpaAdapter.java` | 번역 포트 구현체 | [ V ] |
| 15 | `extranet/accommodation/adapter/in/web/AccommodationDetailResponse.java` | 응답 DTO, snake_case | [ V ] |

## FLOW 1. 피드백
1. Mapper를 직접 정의하지 않고 MapStruct를 사용하는게 좋다고 판단
2. AccommodationType에 해외숙소, 연동된 호텔이면 HOTEL값을 그대로 사용해도 될지?
3. AccommodationJpaEntity 에 외부연동 여부를 그럼 숙소 파트너가 ON/OFF할 수 있는건지?

---

## FLOW 2. 숙소 수정 (파트너) + 관리자 승인

> `PUT /api/v1/extranet/accommodations/{id}` → `PATCH /api/v1/admin/accommodations/{id}/approve-modification`

| 순서 | 파일 | 확인 포인트 | 완료    |
|------|------|-------------|-------|
| 1 | `extranet/accommodation/adapter/in/web/UpdateAccommodationRequest.java` | Double 타입 위경도, nullable 필드 | [ V ] |
| 2 | `extranet/accommodation/application/port/in/ExtranetUpdateAccommodationUseCase.java` | Command nullable 처리 | [ V ] |
| 3 | `extranet/accommodation/application/service/ExtranetUpdateAccommodationService.java` | ModificationRequest 생성 로직 | [ V ] |
| 4 | `core/accommodation/adapter/out/persistence/AccommodationModificationRequestJpaEntity.java` | JSON 저장 구조 | [ V ] |
| 5 | `core/accommodation/domain/enums/ModificationStatus.java` | PENDING/APPROVED/REJECTED | [ V ] |
| 6 | `admin/accommodation/application/port/in/AdminApproveModificationUseCase.java` | 승인 커맨드 | [ V ] |
| 7 | `admin/accommodation/application/service/AdminApproveModificationService.java` | JSON → 도메인 반영, null 필드 보존 | [ V ] |
| 8 | `admin/accommodation/adapter/in/web/AdminAccommodationController.java` | 관리자 엔드포인트 | [ V ] |

## FLOW 2. 피드백
1. ~~숙소 다국어 관련 상세 정보는 수정은 못하는거야?~~ → [V] `UpdateAccommodationRequest`에 `translations` 필드 추가 완료
2. ~~ExtranetUpdateAccommodationUseCase.java에서 AccommodationModificationRequestJpaRepository를 직접 호출하는게 결합도가 높아지는거 아닌가?~~ → [V] `PersistModificationRequestPort` + `LoadModificationRequestPort` 분리, `ModificationRequestJpaAdapter` 구현
3. ~~AdminApproveModificationUseCase여기에 조회 기능을 따로 빼자. 약속을 그렇게 한거같은데.~~ → [V] `AdminGetModificationQuery` 분리, `ModificationRequestSummary` record 반환 (JPA 엔티티 노출 제거)
4. ~~RejectModificationRequest 그냥 밖으로 빼서 파일로 만들어.~~ → [V] 별도 파일로 추출 완료

---

## FLOW 3. 숙소 목록/상세 조회 (관리자)

> `GET /api/v1/admin/accommodations` / `GET /api/v1/admin/accommodations/{id}`

| 순서 | 파일 | 확인 포인트 | 완료    |
|------|------|-------------|-------|
| 1 | `admin/accommodation/application/port/in/AdminListAccommodationQuery.java` | listAll / listByStatus / getDetailById | [ V ] |
| 2 | `admin/accommodation/application/service/AdminListAccommodationService.java` | 번역 포함 조회, 상태 필터 | [ V ] |
| 3 | `core/accommodation/application/port/out/LoadAccommodationPort.java` | 조회 포트, findByStatus | [ V ] |
| 4 | `core/accommodation/application/port/out/LoadAccommodationTranslationPort.java` | 번역 조회 포트 | [ V ] |
| 5 | `core/accommodation/adapter/out/persistence/AccommodationJpaRepository.java` | 쿼리 메서드, findByStatus | [ V ] |
| 6 | `core/accommodation/adapter/out/persistence/AccommodationTranslationJpaRepository.java` | 번역 쿼리 | [ V ] |
| 7 | `admin/accommodation/adapter/in/web/AdminAccommodationDetailResponse.java` | 번역 포함 응답 | [ V ] |

## FLOW 3. 피드백
1. ~~Optional<SupplierAccommodationMappingJpaEntity> findBySupplierIdAndExternalAccommodationId 미사용 메서드는 왜 만든건지?~~ → 확인 완료. Supplier 연동 시 사용 예정으로 선언된 메서드. 현재 미사용이나 의도적 유지.

---

## FLOW 4. 객실/객실옵션 등록 (파트너)

> `POST /api/v1/extranet/accommodations/{id}/rooms`

| 순서 | 파일 | 확인 포인트 | 완료    |
|------|------|-------------|-------|
| 1 | `extranet/room/adapter/in/web/RegisterRoomRequest.java` | 필드 검증 | [ V ] |
| 2 | `extranet/room/adapter/in/web/ExtranetRoomController.java` | Room + RoomOption 엔드포인트 | [ V ] |
| 3 | `extranet/room/application/port/in/ExtranetRegisterRoomUseCase.java` | Command 정의 | [ V ] |
| 4 | `extranet/room/application/service/ExtranetRegisterRoomService.java` | 숙소 존재 확인, Room 생성 | [ V ] |
| 5 | `core/room/domain/model/Room.java` | 불변식, 상태 | [ V ] |
| 6 | `core/room/domain/model/RoomOption.java` | 옵션 모델 | [ V ] |
| 7 | `core/room/adapter/out/persistence/RoomMapper.java` | 도메인 ↔ JPA 변환 | [ V ] |
| 8 | `core/room/adapter/out/persistence/RoomJpaEntity.java` | 테이블 정의 | [ V ] |
| 9 | `core/room/adapter/out/persistence/RoomOptionJpaEntity.java` | 옵션 테이블 | [ V ] |
| 10 | `core/room/adapter/out/persistence/RoomJpaAdapter.java` | 저장 구현 | [ V ] |
| 11 | `core/room/adapter/out/persistence/RoomOptionJpaAdapter.java` | 옵션 저장 구현 | [ V ] |
| 12 | `extranet/room/adapter/in/web/RoomDetailResponse.java` | 응답 DTO | [ V ] |

## FLOW 4. 피드백
1. ~~RoomMapper.java에 default Room toDomain(RoomJpaEntity entity) 이런식으로 직접 구현할 필요가 없지않아?~~ → [V] MapStruct `@Mapping` + `@AfterMapping` 패턴으로 전환 완료
2. ~~RoomOptionJpaEntity 얘는 다국어 처리가 없나봐?~~ → [V] `RegisterRoomOptionRequest`에 translations 추가, `PersistRoomOptionTranslationPort` + Adapter 생성 완료

---

## FLOW 5. 재고 설정 (파트너)

> `POST /api/v1/extranet/room-options/{id}/inventories` (숙박)
> `POST /api/v1/extranet/room-options/{id}/time-slots` (대실)

| 순서 | 파일 | 확인 포인트 | 완료     |
|------|------|-------------|--------|
| 1 | `extranet/inventory/adapter/in/web/ExtranetInventoryController.java` | 숙박 재고 설정/조회 엔드포인트 | [ V ]  |
| 2 | `extranet/inventory/adapter/in/web/ExtranetHourlySettingController.java` | 대실 설정 엔드포인트 | [ V ]  |
| 3 | `extranet/inventory/adapter/in/web/SetInventoryRequest.java` | 날짜 범위, 재고 수량 | [ V ]  |
| 4 | `extranet/inventory/adapter/in/web/OpenTimeSlotsRequest.java` | 시간 슬롯 요청 | [ V ]  |
| 5 | `extranet/inventory/application/port/in/ExtranetSetInventoryUseCase.java` | Command | [ V ]  |
| 6 | `extranet/inventory/application/service/ExtranetSetInventoryService.java` | 날짜별 재고 생성/수정 | [ V ]  |
| 7 | `extranet/inventory/application/service/ExtranetSetTimeSlotInventoryService.java` | 시간 슬롯 생성 | [ V ]  |
| 8 | `extranet/inventory/application/service/ExtranetSetHourlySettingService.java` | 대실 기본 설정 저장 | [ V ]  |
| 9 | `core/inventory/domain/model/Inventory.java` | 재고 도메인, 차감/복원 메서드 | [ V ]  |
| 10 | `core/inventory/domain/model/TimeSlotInventory.java` | 시간 슬롯 모델, block 메서드 | [ V ]  |
| 11 | `core/inventory/domain/service/InventoryDomainService.java` | 재고 비즈니스 규칙 | [ V ]  |
| 12 | `core/inventory/adapter/out/persistence/InventoryMapper.java` | 변환 + restoreTimestamps | [ V ]  |
| 13 | `core/inventory/adapter/out/persistence/InventoryJpaEntity.java` | 재고 테이블 | [ V ]  |
| 14 | `core/inventory/adapter/out/persistence/TimeSlotInventoryJpaEntity.java` | 시간 슬롯 테이블 | [ V ]  |
| 15 | `core/inventory/adapter/out/persistence/InventoryJpaAdapter.java` | 저장 구현, STAY→HOURLY 충돌 방지 | [ V ] |

## FLOW 5. 재고 설정 (파트너) 피드백
0. 숙소 파트너 설정에서 30분, 60분 설정이 가능하게 중간에 기획의도가 변경되었는데, 이슈될 요소가 있는지 체크가 필요.
1. 객실 옵션이 1개 남았을때, 대실이 숙박의 체크인~체크아웃 사이에 있다면 숙박은 예약이 안되고, 숙박이 되었다면 숙박 체크인~체크아웃 시간 사이는 대실이 안되도록 구현되어있는지
2. `ExtranetSetInventoryUseCase.java` 에서 dates != null && !dates.isEmpty() ->  !CollectionUtils.isEmpty(dates) 이런식 표현이 더 명확해 보이지 않은지
3. `ExtranetSetTimeSlotInventoryService.java`, `ExtranetSetHourlySettingService.java`에서 AccommodationHourlySettingJpaRepository 직접 참조가 맞는지?
4.  `InventoryDomainService.java` 미사용인데 이유

---

## FLOW 6. 가격 설정 (파트너)

> `POST /api/v1/extranet/room-options/{id}/prices`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `extranet/price/adapter/in/web/ExtranetPriceController.java` | `@Valid` 적용, Inbound Port만 의존, `ApiResponse` 래핑 | [ V ] |
| 2 | `extranet/price/adapter/in/web/SetPriceRequest.java` | BigDecimal 금액 필드, `@NotNull`/`@NotBlank` 검증 | [ V ] |
| 3 | `extranet/price/application/port/in/ExtranetSetPriceUseCase.java` | Command record, `getTargetDates()` 날짜범위/개별날짜 양쪽 지원 | [ V ] |
| 4 | `extranet/price/application/service/ExtranetSetPriceService.java` | `@Transactional`, 소유권 3단계 검증(옵션→객실→숙소), 기존 가격 있으면 업데이트 | [ V ] |
| 5 | `core/price/domain/model/RoomPrice.java` | BigDecimal, 불변식(null/음수 검증), `updatePrice` 상태 변경 메서드 | [ V ] |
| 6 | `core/price/domain/service/PriceDomainService.java` | 순수 Java, `@Service` 없음, `DomainServiceConfig`에서 `@Bean` 등록, VAT 계산 | [ V ] |
| 7 | `core/price/adapter/out/persistence/RoomPriceMapper.java` | MapStruct, `@AfterMapping` restoreTimestamps | [ V ] |
| 8 | `core/price/adapter/out/persistence/RoomPriceJpaEntity.java` | 필드 주석 완비, `BaseJpaEntity` 상속, `precision=12, scale=2` | [ V ] |
| 9 | `core/price/adapter/out/persistence/RoomPriceJpaAdapter.java` | `PersistRoomPricePort` + `LoadRoomPricePort` 구현, `saveAllAndFlush` | [ V ] |

## FLOW 6. 피드백
1. `ExtranetSetPriceService`와 `ExtranetGetPriceService`의 소유권 검증 로직(옵션→객실→숙소→partnerId)이 완전히 동일하게 중복됨. FLOW 5에서도 비슷한 패턴 지적. 공통 도메인 서비스나 헬퍼로 추출 검토 필요.
2. `SetPriceRequest.priceType`이 `@NotBlank String`으로 되어있어서, 잘못된 값(예: "INVALID") 입력 시 Service에서 `PriceType.valueOf()`가 `IllegalArgumentException` 발생 → 500 에러. Request DTO에서 `PriceType` Enum으로 직접 받으면 Jackson이 변환하고 실패 시 자동 400 반환.
3. `final-checklist.md`에 "DomainServiceConfig 삭제됨"이라 기술되어 있으나, 실제로는 `common/config/DomainServiceConfig.java`가 존재하며 `PriceDomainService`, `InventoryDomainService`를 `@Bean` 등록 중. 문서-코드 불일치 수정 필요.

---

## FLOW 7. 숙소 검색 (고객)

> `GET /api/v1/accommodations?check_in_date=...&check_out_date=...`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `customer/accommodation/adapter/in/web/SearchAccommodationRequest.java` | 검색 파라미터 record, `toCriteria()` 변환 | [ V ] |
| 2 | `customer/accommodation/adapter/in/web/CustomerAccommodationController.java` | 검색 + 상세 엔드포인트, `@PageableDefault`, `@DateTimeFormat` | [ V ] |
| 3 | `customer/accommodation/application/port/in/CustomerSearchAccommodationQuery.java` | Inbound Port, `Page<AccommodationSummary>` 반환 | [ V ] |
| 4 | `customer/accommodation/application/service/CustomerSearchAccommodationService.java` | 4단계 검색(ID→카드→최저가→조립), `@Transactional(readOnly=true)`, locale 반영 | [ V ] |
| 5 | `core/accommodation/application/port/out/SearchAccommodationPort.java` | `searchIds`, `loadLowestPrices`, `SearchCriteria`/`AccommodationSummary` record 정의 | [ V ] |
| 6 | `core/accommodation/adapter/out/persistence/AccommodationSearchJpaAdapter.java` | QueryDSL, 2단계 배치(ID→이미지/최저가), EXISTS 서브쿼리 가격 필터, 지역 서브트리 재귀 | [ V ] |
| 7 | `customer/accommodation/application/port/in/CustomerGetAccommodationDetailQuery.java` | 상세 조회 record 계층 (`AccommodationDetail` → `RoomWithOptions` → `OptionWithPrice`) | [ V ] |
| 8 | `customer/accommodation/application/service/CustomerGetAccommodationDetailService.java` | 상세 + 가격/재고 조합, 번역 배치 로드, VAT 반영 | [ V ] |

## FLOW 7. 피드백
1. `SearchAccommodationRequest` 미사용하면 제거
2. **`SearchCriteria.accommodationType`가 String**: `AccommodationSearchJpaAdapter.buildWhereClause()`에서 `AccommodationType.valueOf()`로 변환하는데, 잘못된 값 입력 시 500 에러. FLOW 6 `priceType`과 동일한 패턴 — Enum으로 받거나 검증 추가 필요.
3. **`SearchAccommodationPort.search()` 메서드 미사용**: Service에서는 `searchIds()` + `loadLowestPrices()`만 사용. 기존 `search()` 메서드와 그 구현체(`AccommodationSearchJpaAdapter.search()`)가 dead code로 남아있음. 삭제 검토.
4. **`CustomerGetAccommodationDetailService` 아키텍처 위반 — adapter 직접 참조**: Application 레이어(service)에서 `core` 패키지의 adapter 레이어 클래스를 직접 import하고 있음:
   - `AccommodationTranslationJpaEntity` (adapter/out/persistence)
   - `RoomTranslationJpaEntity`, `RoomOptionTranslationJpaEntity` (adapter/out/persistence)
   - `RoomImageJpaRepository`, `RoomImageJpaEntity` (adapter/out/persistence)
   - 헥사고날 원칙 위반 (application → adapter 의존). Port를 통해 접근하거나, Translation 포트 반환 타입을 도메인 모델/record로 변경 필요.
5. **옵션별 N+1 가능성**: `buildOptionWithPrice()`에서 옵션마다 `loadRoomPricePort` + `loadInventoryPort`를 개별 호출. 옵션 수가 많으면 N+1. 배치 조회(`findByRoomOptionIdIn`)로 전환 검토.

---

## FLOW 8. 예약 생성 (고객)

> `POST /api/v1/reservations/stay` / `/hourly`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `customer/reservation/adapter/in/web/CreateStayReservationRequest.java` | `@NotNull`/`@NotBlank` 검증, 멱등성 키(`reservationRequestId`), `toCommand(memberId)` | [ V ] |
| 2 | `customer/reservation/adapter/in/web/CreateHourlyReservationRequest.java` | `LocalTime` startTime/endTime, `@NotNull` 검증 | [ V ] |
| 3 | `customer/reservation/adapter/in/web/CustomerReservationController.java` | STAY/HOURLY 분리 엔드포인트, 결제확인/취소/조회, `X-Member-Id` 헤더, `ApiResponse` 래핑 | [ V ] |
| 4 | `customer/reservation/application/port/in/CustomerCreateReservationUseCase.java` | STAY/HOURLY Command record 분리, `LocalTime`/`LocalDate` 사용 | [ V ] |
| 5 | `customer/reservation/application/service/CustomerCreateReservationService.java` | 멱등성 검사, STAY↔HOURLY 충돌 사전 확인, 비관적 락 재고 차감, 가격 계산(VAT), Hold 만료 설정(10분), 버퍼 슬롯 차단 | [ V ] |
| 6 | `core/reservation/domain/model/Reservation.java` | 상태 머신(PENDING→PAYMENT_WAITING→CONFIRMED→COMPLETED/CANCELLED/NO_SHOW), `holdForPayment`/`confirm`/`cancel` 불변식, `reconstruct`, `isHoldExpired` | [ V ] |
| 7 | `core/reservation/domain/model/GuestInfo.java` | record VO, compact constructor null/blank 검증 | [ V ] |
| 8 | `core/reservation/domain/enums/ReservationStatus.java` | 6개 상태 정의 | [ V ] |
| 9 | `core/reservation/domain/event/ReservationCreatedEvent.java` | POJO record, `occurredAt` Instant | [ V ] |
| 10 | `core/reservation/adapter/out/persistence/ReservationMapper.java` | `toDomain` reconstruct 사용, `toJpaEntity` GuestInfo→필드 매핑, `restoreTimestamps` | [ V ] |
| 11 | `core/reservation/adapter/out/persistence/ReservationJpaEntity.java` | 필드 주석 완비, `BaseJpaEntity` 상속, `reservationRequestId` unique, `BigDecimal precision=12` | [ V ] |
| 12 | `core/reservation/adapter/out/persistence/ReservationJpaAdapter.java` | `PersistReservationPort` + `LoadReservationPort` 구현, `saveAndFlush` | [ V ] |
| 13 | `customer/reservation/adapter/in/web/ReservationResponse.java` | 전체 필드 포함 응답 DTO, `from(Reservation)` 변환 | [ V ] |

## FLOW 8. 피드백
1. **`ReservationCreatedEvent` 미발행**: `CustomerCreateReservationService`에서 예약 생성 후 `ReservationCreatedEvent`를 발행하지 않음. 이벤트 record는 정의되어 있으나 실제 `ApplicationEventPublisher.publishEvent()` 호출이 없음. 이벤트 기반 아키텍처 설계 의도상 발행 추가 또는 의도적 미발행이면 이벤트 record 삭제 검토.
 -> 의도적으로 구현만 한거고 상세 로직은 설계정도에서 머물기에 냅둬도됨
2. **멱등성 처리가 예외 발생 방식**: 동일 `reservationRequestId` 재요청 시 `ifPresent`로 `BusinessException`을 던짐. 멱등성 본래 의미는 "같은 요청에 같은 응답을 반환"이므로, 기존 예약을 그대로 반환하는 게 더 적절할 수 있음. 현재 방식이면 클라이언트가 네트워크 재시도 시 에러를 받게 됨.
 -> 예약 요청이 두 번 들어가는건 문제가 있음. 확인 바람
3. **`Reservation.reconstruct()`에서 `reservationNumber` 재생성 후 덮어쓰기**: Builder 생성자에서 `generateReservationNumber()`가 호출되고, `reconstruct`에서 바로 `r.reservationNumber = reservationNumber`로 덮어씀. 불필요한 UUID 생성이 매번 발생. `reconstruct` 전용 private 생성자 분리 검토.
 -> 진행

---

## FLOW 9. 예약 확정/취소 (파트너)

> `PATCH /api/v1/extranet/reservations/{id}/confirm` / `.../cancel`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `extranet/reservation/adapter/in/web/ExtranetReservationController.java` | confirm/cancel/조회 엔드포인트, `CancelReservationRequest` inner record, `X-Partner-Id` 헤더 | [ V ] |
| 2 | `extranet/reservation/application/port/in/ExtranetConfirmReservationUseCase.java` | 단순 인터페이스, `Reservation` 반환 | [ V ] |
| 3 | `extranet/reservation/application/service/ExtranetConfirmReservationService.java` | `@Transactional`, `reservation.confirm()` 도메인 상태 전이 | [ V ] |
| 4 | `extranet/reservation/application/port/in/ExtranetCancelReservationUseCase.java` | `(reservationId, partnerId, reason)` 시그니처 | [ V ] |
| 5 | `extranet/reservation/application/service/ExtranetCancelReservationService.java` | 소유권 검증, 상태 전이, STAY 재고 복원(비관적 락), 로깅 | [ V ] |
| 6 | `core/reservation/domain/event/ReservationConfirmedEvent.java` | POJO record, `occurredAt` Instant | [ V ] |
| 7 | `core/reservation/domain/event/ReservationCancelledEvent.java` | POJO record, `roomOptionId` 포함 (재고 복구용) | [ V ] |
| 8 | `core/reservation/adapter/scheduler/HoldExpirationScheduler.java` | `@Scheduled(fixedDelay=60000)`, STAY/HOURLY 분기 재고 복구, 비관적 락, 버퍼 슬롯 해제 | [ V ] |

## FLOW 9. 피드백
1. **`ExtranetConfirmReservationService.confirm()`에 소유권 검증 없음**: 예약 ID만으로 확정 가능. `X-Partner-Id` 헤더가 Controller 시그니처에도 없음(`cancel`에는 있음). 다른 파트너의 예약도 확정할 수 있는 보안 이슈.
-> 숙소 파트너만 승인가능하도록
2. **`ExtranetReservationController`에서 `customer` 패키지의 `ReservationResponse` 직접 참조**: `import com.accommodation.platform.customer.reservation.adapter.in.web.ReservationResponse` — 채널 간 직접 참조 금지 규칙 위반. Extranet 전용 Response를 만들거나, 공통 Response를 `core`로 이동 필요.
-> 이동
3. **`ExtranetCancelReservationService`의 소유권 검증**: FLOW 6에서 만든 `ExtranetOwnershipVerifier`를 사용하지 않고 직접 `loadAccommodationPort` 호출. 통일 검토.
-> 재사용 권장
4. **HOURLY 예약 취소 시 재고 복원 누락**: `ExtranetCancelReservationService.cancel()`에서 STAY만 재고 복원하고 HOURLY는 처리하지 않음. `HoldExpirationScheduler`에는 HOURLY 복원(`restoreHourlyInventory`) 로직이 있으므로, 파트너 수동 취소에서도 동일 로직 필요.
-> 진행
5. **`HoldExpirationScheduler.restoreHourlyInventory()`에서 버퍼 시간 하드코딩**: `+ 30L`로 고정. 실제 숙소별 `slotUnitMinutes`가 30/60 등 다를 수 있으므로 `LoadHourlySettingPort`에서 조회하거나, 예약 시 버퍼 범위를 같이 저장하는 것이 정확.
-> 진행

---

## FLOW 10. 태그 관리 (관리자)

> `POST /api/v1/admin/tag-groups` → `POST /api/v1/admin/tag-groups/{id}/tags`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `admin/tag/adapter/in/web/AdminTagController.java` | 그룹/태그 CRUD + activate 엔드포인트 | [   ] |
| 2 | `admin/tag/adapter/in/web/CreateTagGroupRequest.java` | 필드 검증 | [   ] |
| 3 | `admin/tag/adapter/in/web/UpdateTagGroupRequest.java` | Integer displayOrder (nullable) | [   ] |
| 4 | `admin/tag/application/port/in/AdminManageTagGroupUseCase.java` | Command + activate | [   ] |
| 5 | `admin/tag/application/service/AdminManageTagGroupService.java` | 그룹 CRUD 로직 | [   ] |
| 6 | `core/tag/domain/model/TagGroup.java` | updateInfo null-guard, activate/deactivate | [   ] |
| 7 | `core/tag/domain/model/Tag.java` | updateInfo null-guard, activate/deactivate | [   ] |
| 8 | `admin/tag/application/port/in/AdminManageTagUseCase.java` | Command + activate | [   ] |
| 9 | `admin/tag/application/service/AdminManageTagService.java` | findAllByTagGroupId (비활성 포함) | [   ] |
| 10 | `core/tag/application/port/out/LoadTagPort.java` | findByTagGroupId / findAllByTagGroupId | [   ] |
| 11 | `core/tag/application/port/out/LoadTagGroupPort.java` | findById / findAll | [   ] |
| 12 | `core/tag/adapter/out/persistence/TagJpaAdapter.java` | 두 조회 메서드 구현 | [   ] |
| 13 | `core/tag/adapter/out/persistence/TagJpaRepository.java` | active 필터 유무 메서드 | [   ] |
| 14 | `core/tag/adapter/out/persistence/TagGroupJpaEntity.java` | 태그 그룹 테이블 | [   ] |
| 15 | `core/tag/adapter/out/persistence/TagJpaEntity.java` | 태그 테이블 | [   ] |

---

## FLOW 11. 태그 조회 (파트너 — 숙소에 태그 등록)

> `GET /api/v1/extranet/accommodations/{id}/tags/groups` → `POST /api/v1/extranet/accommodations/{id}/tags`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `extranet/tag/adapter/in/web/ExtranetTagController.java` | 조회 + 등록/삭제 엔드포인트 | [   ] |
| 2 | `extranet/tag/application/port/in/ExtranetGetAvailableTagQuery.java` | 숙소 유형별 태그 조회 | [   ] |
| 3 | `extranet/tag/application/service/ExtranetGetAvailableTagService.java` | 활성 태그만 필터 | [   ] |
| 4 | `extranet/tag/application/port/in/ExtranetManageAccommodationTagUseCase.java` | 태그 연결/해제 | [   ] |
| 5 | `extranet/tag/application/service/ExtranetManageAccommodationTagService.java` | 태그 연결 로직 | [   ] |
| 6 | `core/tag/adapter/out/persistence/AccommodationTagJpaEntity.java` | 숙소-태그 연결 테이블 | [   ] |
| 7 | `core/tag/adapter/out/persistence/AccommodationTagJpaRepository.java` | 연결 쿼리 | [   ] |

---

## FLOW 12. 외부 공급사 연동

> `POST /api/v1/supplier/{supplierCode}/sync`

| 순서 | 파일 | 확인 포인트 | 완료 |
|------|------|-------------|------|
| 1 | `core/supplier/application/port/in/SyncSupplierInventoryUseCase.java` | 동기화 커맨드 | [   ] |
| 2 | `core/supplier/application/service/SyncSupplierInventoryService.java` | 외부 데이터 → 내부 매핑 | [   ] |
| 3 | `core/supplier/application/port/out/SupplierClient.java` | 외부 API 포트 | [   ] |
| 4 | `core/supplier/adapter/out/external/MinhyukHouseSupplierAdapter.java` | 클라이언트 구현 (샘플 데이터 반환) | [   ] |
| 5 | `core/supplier/domain/model/CanonicalAccommodation.java` | 정규화 모델 | [   ] |
| 6 | `core/supplier/domain/model/CanonicalRoom.java` | 정규화 객실 | [   ] |
| 7 | `core/supplier/domain/model/CanonicalPrice.java` | 정규화 가격 | [   ] |
| 8 | `core/supplier/adapter/out/persistence/SupplierAccommodationMappingJpaEntity.java` | 외부 ID 매핑 테이블 | [   ] |
| 9 | `core/supplier/adapter/out/persistence/SupplierRoomMappingJpaEntity.java` | 객실 매핑 | [   ] |

---

## 공통 인프라 (모든 플로우 공통)

| 파일 | 확인 포인트 | 완료 |
|------|-------------|------|
| `common/adapter/out/persistence/BaseJpaEntity.java` | createdAt/updatedAt, restoreTimestamps | [   ] |
| `common/domain/BaseEntity.java` | 도메인 기본 필드 | [   ] |
| `common/domain/SoftDeletable.java` | is_deleted soft delete | [   ] |
| `common/exception/ErrorCode.java` | HTTP 상태 + 비즈니스 코드 | [   ] |
| `common/exception/BusinessException.java` | RuntimeException 계열 | [   ] |
| `common/exception/GlobalExceptionHandler.java` | 전역 예외 처리, 404/400/500 | [   ] |
| `common/response/ApiResponse.java` | SUCCESS/ERROR 포맷 | [   ] |
| `common/config/SecurityConfig.java` | JWT 인증/인가, URL별 Role 접근 제어, accessDeniedHandler | [   ] |
| `common/security/JwtTokenProvider.java` | JWT 생성/검증 (HMAC-SHA512) | [   ] |
| `common/security/JwtAuthenticationFilter.java` | Bearer 토큰 → SecurityContext + X-Partner-Id/X-Member-Id 헤더 자동 주입 | [   ] |
| `common/security/AuthController.java` | 토큰 발급 엔드포인트 (Demo용) | [   ] |
| `common/config/JacksonConfig.java` | snake_case, Instant 직렬화 | [   ] |
| `common/config/QuerydslConfig.java` | JPAQueryFactory 빈 | [   ] |
| `common/filter/MdcLoggingFilter.java` | trace_id MDC 설정 | [   ] |
| `common/interceptor/RequestLoggingInterceptor.java` | 요청/응답 로깅 | [   ] |

---

## API 테스트 페이지

| 파일 | 확인 포인트 | 완료    |
|------|-------------|-------|
| `src/main/resources/static/api-test.html` | 역할별 토큰 발급, 6개 시나리오 실행(S-A1/S-P1/S-C1~C4), 보안 테스트 | [ V ] |
