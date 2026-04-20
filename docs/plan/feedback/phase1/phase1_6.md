# Phase 1 피드백

> 아래에 피드백을 작성해주세요. Claude가 이 파일을 읽고 반영합니다.

## 피드백

### 1
> 태그도 다국어를 생각하면 `_translation` 필요함

### 2
> 테스트 코드 추가 필요. `@admin-api.adoc` 최신화가 안되어있음

### 3
> 숙소 파트너가 자신들의 숙소의 어떤 태그를 추가/수정하는 API가 존재하지 않음.

### 4
> `src/main/java/com/accommodation/platform/extranet/tag/adapter/in/web/ExtranetTagController.java`에 
> getTagsByGroup 메서드에 `@RequestHeader("X-Partner-Id") Long partnerId` 누락

### 5
> '@extranet-api.adoc' 최신화 필요