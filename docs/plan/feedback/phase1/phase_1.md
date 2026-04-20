# Phase 1 피드백

> 아래에 피드백을 작성해주세요. Claude가 이 파일을 읽고 반영합니다.

## 피드백

### 1
> model안에서 enum을 두지말고, 나는 그 상위로 올라가면 좋을거 같아. adapter, application, domain. enums 이런식

### 2
> `AccommodationType.java`, `RoomType` 숙소유형은 그나마 정해져있을거 같은데, 객실 유형은 저렇게 딱 정해질 수 있나 ? 
>  그렇다고 숙소마다 다 객실타입을 관리하기에는 데이터 row수가 걱정이긴한데, 아이디어 있을까?

### 3
> 이미지 url을 저장하는거 같은데, 풀패스로 저장하진 말아줘 /accommodation/{category}/20260420102838.png 등 이런식으로 나중에 이미지 저장소를 변경해야 할 때 리스크가 덜 할꺼야.

### 4
> 그리고 지금 `AccommodationJpaEntity.java`에 숙소 설명 정보가 들어가 있는거면 이렇게 하면 안되고 'AccommodationTranslationJpaEntity.java' 이런식으로 각 항목에 대한 언어별 데이터로 저장할 수 있게 해줘.
> 상세정보로는 숙소소개, 서비스 및 부대시설(유형별 관리자가 등록하면 숙소파트너가 자신들에게 해당하는 값들만 체크), 숙소 이용 정보, 취소 및 환불 규정 정도만 일단 만들자

### 5
> 주소 관련된 값이 많은데 일단 나는 숙소에게 위치 관련 정보로는 풀주소(도로명,지번 등) 하나, 위도, 경도, 위치관련 설명 이렇게 받을꺼고, 여기서 풀주소랑 위치관련 설명에는 translation 되야하는거 체크해줘.

### 6
> `AccommodationJpaEntity.java`에 partnerId는 큰 의미로 보면 업체번호라고 생각해볼게 나중에 판매자 정보를 보여줘야할 수 있어.
> 추가로 궁금한건 해당 partnerId가 있는 사람만 수정할 수 있는 권한을 주려고 넣은건 아니지?

### 7
> `src/main/java/com/accommodation/platform/core/accommodation/application/port/out/AccommodationRepository.java` 해당 port명 보다는
> `SaveAccommodationOutPort`, `UpdateAccommodationOutPort`, `DeleteAccommodationOutPort`, `LoadAccommodationOutPort` 이런식으로 표현하는게 어떨지
> 아니면 너무 분산되면 등록/수정/삭제 묶고, 조회용 하나로 해도 될거같아. ( 아이디어 있으면 제안해줘. )
> 전체적인 port 패키지 안에 네이밍 수정필요

### 8
> 그리고 @Table 있는곳엔, 해당 필드가 무슨 의미인지 주석으로 표시

### 9
> `RoomOptionJpaEntity.java` breakfastIncluded 필드는 어떻게 활용할건지 얘기해줘.

### 10
> `src/main/java/com/accommodation/platform/admin/accommodation/adapter/in/web/AdminAccommodationController.java`
> 관리자가 숙소를 승인,거절,닫는거를 할 수가 있나? 지금 코드를 보면 의도가 숙소파트너가 자신들의 숙소를 등록하고, 심사를 받는 구조인거같은데, 숙박플랫폼 기본 구조가 이런가
> 이런 구조가 많고, 일반적이라면 현재 방식대로 그대로 가자.
> 우려사항은 숙소파트너가 자기마음대로 이상한 단어를 써서 객실이라든지 상세내용을 표기한다면 우리 플랫폼 관리자 측에서는 매우 난처한 상황이 올거같은데
> 데이터의 등록/수정/삭제를 숙소 파트너에게 어느정도까지 줘야하는지 아니면 최초 등록 후 수정마다 관리자의 승인을 받는 구조인거면 그나마 안전장치가 생기는거 같은데