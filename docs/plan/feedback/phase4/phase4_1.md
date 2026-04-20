# Phase 4 피드백

> 아래에 피드백을 작성해주세요. Claude가 이 파일을 읽고 반영합니다.

## 피드백

### 1
> `src/main/java/com/accommodation/platform/core/reservation/application/service/HoldExpirationScheduler.java`
> 배치도는 간격이 10분단위인데, 만약에 배치가 돌고 1분뒤에 PAYMENT_WAITING 상태인 예약이 생성되면 다음 배치때는 만료가 안될거 같은데 확인해줘.
> 단순 스케쥴도는 로직은 로우 제거를 위한것인지 파악필요. 그리고 패키지 구조를 service에 넣으면 성격이 모호해져서 다른 패키지 구조로 변경 필요.

### 2
> `src/main/java/com/accommodation/platform/core/reservation/adapter/out/persistence/ReservationJpaEntity.java`
> 에 roomId를 안넣은건 roomOptionId로 충분해서 그런거지?

### 3
> `src/main/java/com/accommodation/platform/core/reservation/adapter/out/persistence/ReservationJpaEntity.java`
> 해당 예약에 나중에 쿠폰이나 포인트 같은 부가 서비스 기능이 확장되어야 할때, 어떤식으로 처리할 것인지 고려되었는지 체크바람

### 4
> ReservationMapper 직접 구현하고 있는데, Mapstruct 사용으로 간편화해서 구현 제안

### 5
> `src/main/java/com/accommodation/platform/core/reservation/domain/event/ReservationCancelledEvent.java`
> 추후 리스너를 구현하는지 파악

### 6
> `src/main/java/com/accommodation/platform/extranet/reservation/adapter/in/web/ExtranetReservationController.java`
> 예약확정 API는 존재하는데, 예약취소 API도 필요함. ( 숙소파트너의 상황에 따라 취소처리가 필요함. )

### 7
> admin 관리자의 권한에 대해서 관리자는 숙소 예약확정처리, 취소처리, 숙소 가격, 재고 등 모든걸 할 수 있는 권한이 필요할지 
> 아니면 온전히 숙소 파트너에게 맡기는지 애매함. 보통 슈퍼관리자가 하나쯤은 있어야한다고 생각