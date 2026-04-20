# Phase 5 피드백

## 피드백

### 1 (구두 협의)
> 외부업체가 추가될 수 있으므로 전략패턴으로 확장성 고려.
> SupplierClient 인터페이스 + 공급사별 어댑터(@Component) 구조로 설계.
> 새 공급사 추가 시 SupplierClient 구현체만 추가하면 자동 인식 (Spring DI List 주입).
> SyncSupplierInventoryService에서 supplierCode로 해당 클라이언트를 찾아 호출.

### 2
> 외부업체가 금액에 대한 정보를 실시간으로 줄 수 없는 경우
> 우리의 가격 데이터에 관리자가 설정을해서 예약을 매칭할 수 있도록 작업 필요
> 그러면 숙소 데이터에 해당 숙소는 연동숙소인지 플래그 및 실시간 가격 연동 플래그 필요
> 그리고 연동숙소라면 해당 숙소에 대한 업체숙소ID, 객실에는 업체 객실 ID가 있다면 매칭이 되어야하고, 객실옵션도 마찬가지로 업체연동 아이디들을 매칭할 수 있는 컬럼이 필요함.

### 3
> 현재 코드는 사용자는 hold만 해둔거고 예약자정보 + 결제를 최종 완료해야 예약 요청이 들어가는 구조인거지?

### 4 (구두 협의) — 매핑 구조 설계
> 외부ID를 숙소/객실 엔티티에 직접 넣을지 vs 별도 매핑 테이블로 관리할지 논의.
> 결론: 별도 매핑 테이블 유지. 같은 숙소가 여러 공급사에 동시 등록 가능하고, 도메인 간 결합도 최소화.
> livePriceSync 플래그도 숙소 엔티티가 아닌 supplier_accommodation_mapping 테이블에 배치 (공급사-숙소 관계의 속성).

### 5 (구두 협의) — 3단 매핑 테이블 구조
> 우리 내부 구조(숙소→객실→객실옵션)에 맞춰 3단 매핑 테이블:
> - supplier_accommodation_mapping: 외부숙소 ↔ 내부숙소 + livePriceSync
> - supplier_room_mapping: 외부객실 ↔ 내부객실
> - supplier_room_option_mapping: 외부옵션 ↔ 내부객실옵션
>
> 외부 업체 구조별 대응:
> - 3단 업체: 3개 매핑 테이블 모두 사용
> - 2단 업체 (객실만): room 매핑만, 해당 객실의 첫 번째 옵션에 자동 연결
> - 1단 업체 (옵션만): room_option 매핑 직접 사용
>
> resolveRoomOptionId()에서 room_option_mapping → room_mapping → 첫 번째 옵션 순서로 fallback.

### 6 (구두 협의) — 내부 데이터 필수 존재
> 매핑 테이블은 "동기화 시 외부ID → 내부ID 찾는 용도"이고, 내부 데이터(Accommodation, Room, RoomOption)는 반드시 존재해야 함.
> 고객 검색/상세가 Room 기준으로 조회하므로, Room 없이 RoomOption만 있으면 화면에 안 보임.
> 외부 업체가 옵션만 주더라도 온보딩 시 내부 Room("대표") + RoomOption("기본") 생성 필수.
> 어댑터 개발자가 업체 데이터 구조에 맞게 판단해서 온보딩 — 코드 변경 없이 데이터 세팅으로 해결.
