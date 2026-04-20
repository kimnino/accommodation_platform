# Phase 2 피드백

> 아래에 피드백을 작성해주세요. Claude가 이 파일을 읽고 반영합니다.

## 피드백

### 1
> 공식적으로 모텔의 대실 시간단위는 시스템 설정상 30분으로 확정된걸로 처리할게.
> 보통 다른 플랫폼에서는 숙소마다 파트너가 개별 설정이 될까?

### 2
> `src/main/java/com/accommodation/platform/core/price/adapter/out/persistence/RoomPriceJpaEntity.java`
> 기본가격(숙소의 정가), 판매가격(이벤트, 등 실제로 판매하는 가격) 두 가지 요소로도 충분히 사용되려나?
> 그리고 세금 포함 여부를 넣었는데 만약에 false이면 어디서 돈을 더 받으려고? 그리고 10%를 더 부가하는거야?

### 3
> `src/main/java/com/accommodation/platform/core/room/adapter/out/persistence/RoomOptionJpaEntity.java`
> 해당 Phase 내용은 아닌데, 여기는 왜 다국어 처리가 빠져있지?

### 4 (구두 협의)
> 슬롯 단위를 시스템 상수 30분 고정이 아닌, 파트너가 30분 또는 60분 중 선택하도록 변경.
> 다른 플랫폼에서도 슬롯 단위(해상도)는 보통 시스템/플랫폼 레벨이지만,
> 숙소 특성에 따라 1시간 단위가 자연스러운 경우도 있어 파트너 선택으로 결정.
> `AccommodationHourlySetting.slotUnitMinutes` (30 또는 60만 허용)

### 5 (구두 협의)
> 대실 가격 설정 방법 논의.
> A안: AccommodationHourlySetting에 고정 대실가 → 날짜별 차등 불가.
> B안: RoomPrice에 PriceType(STAY/HOURLY) 추가 → 날짜별·유형별 가격 분리.
> 결론: B안 채택. 같은 API로 priceType만 다르게 요청하면 숙박/대실 가격 각각 설정 가능.
> 주말 대실 가격 차등, 비연속 일자 설정 모두 기존 인프라 그대로 활용.