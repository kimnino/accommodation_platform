# API 검증 체크리스트 (curl 기반)

> 서버 실행 후 (`./gradlew bootRun`) 아래 시나리오를 순서대로 수행합니다.
> 샘플 데이터: 숙소 6개 (HOTEL, RESORT, GUEST_HOUSE, MOTEL, PENSION, POOL_VILLA)

---

## 0. 사전 준비

```bash
docker compose up -d          # MySQL 실행
./gradlew bootRun             # 서버 실행 (8080)
```

---

## 1. 고객 채널 — 숙소 검색 & 상세

### 1-1. 전체 검색 (필터 없음)
```bash
curl -s 'http://localhost:8080/api/v1/accommodations?checkInDate=2026-04-25&checkOutDate=2026-04-26&guestCount=2' | python3 -m json.tool
```
- [ ] status: SUCCESS
- [ ] 6개 숙소가 나오는지 (HOTEL, RESORT, GUEST_HOUSE, MOTEL, PENSION, POOL_VILLA)
- [ ] 각 숙소에 `lowest_price`가 있는지 (재고+가격 설정된 날짜)
- [ ] `has_available_room: true`인지

### 1-2. 지역 필터 검색
```bash
curl -s 'http://localhost:8080/api/v1/accommodations?region=서울&checkInDate=2026-04-25&checkOutDate=2026-04-26&guestCount=2'
```
- [ ] 서울 숙소만 나오는지 (호텔, 게스트하우스, 모텔)

### 1-3. 숙소 유형 필터
```bash
curl -s 'http://localhost:8080/api/v1/accommodations?accommodationType=PENSION&checkInDate=2026-04-25&checkOutDate=2026-04-26&guestCount=2'
```
- [ ] PENSION 유형만 나오는지

### 1-4. 가격 범위 필터
```bash
curl -s 'http://localhost:8080/api/v1/accommodations?minPrice=100000&maxPrice=200000&checkInDate=2026-04-25&checkOutDate=2026-04-26&guestCount=2'
```
- [ ] 최저가가 100,000~200,000원 범위인 숙소만 나오는지

### 1-5. 숙소 상세 (호텔)
```bash
curl -s 'http://localhost:8080/api/v1/accommodations/1?checkInDate=2026-04-25&checkOutDate=2026-04-26' | python3 -m json.tool
```
- [ ] 숙소 정보 (name, type, address, images)
- [ ] rooms 배열: 디럭스 더블, 스위트룸
- [ ] 각 room에 options: 기본/조식포함, 가격 표시
- [ ] remaining_quantity가 재고와 일치하는지

### 1-6. 숙소 상세 (풀빌라)
```bash
curl -s 'http://localhost:8080/api/v1/accommodations/6?checkInDate=2026-04-25&checkOutDate=2026-04-26' | python3 -m json.tool
```
- [ ] 풀빌라 정보 + 객실 1개 (오션뷰 빌라)
- [ ] 옵션 2개 (기본 350,000원, 케이터링 450,000원)

### 1-7. 재고 없는 날짜 검색
```bash
curl -s 'http://localhost:8080/api/v1/accommodations?checkInDate=2026-06-01&checkOutDate=2026-06-02&guestCount=2'
```
- [ ] 숙소는 나오지만 `lowest_price: null`, `has_available_room: false`

---

## 2. 고객 채널 — 예약

### 2-1. 숙박 예약 생성
```bash
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/stay' \
  -d '{
    "reservation_request_id": "'$(uuidgen)'",
    "accommodation_id": 1,
    "room_option_id": 1,
    "check_in_date": "2026-04-25",
    "check_out_date": "2026-04-26",
    "guest_name": "홍길동",
    "guest_phone": "010-1234-5678",
    "guest_email": "hong@test.com"
  }' | python3 -m json.tool
```
- [ ] status: SUCCESS, HTTP 201
- [ ] reservation_number 생성
- [ ] status: PAYMENT_WAITING
- [ ] total_price: 120000 (호텔 디럭스 기본 1박)
- [ ] hold_expired_at이 10분 후로 설정

### 2-2. 멱등성 확인 — 같은 reservation_request_id로 재요청
```bash
# 위에서 사용한 것과 같은 UUID로 다시 요청
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/stay' \
  -d '{
    "reservation_request_id": "<위에서_사용한_UUID>",
    "accommodation_id": 1, "room_option_id": 1,
    "check_in_date": "2026-04-25", "check_out_date": "2026-04-26",
    "guest_name": "홍길동", "guest_phone": "010-1234-5678"
  }'
```
- [ ] 에러 응답 (이미 처리된 예약)

### 2-3. 결제 확인 (PAYMENT_WAITING → CONFIRMED)
```bash
curl -s -X POST -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/{예약ID}/confirm-payment' | python3 -m json.tool
```
- [ ] status: CONFIRMED
- [ ] hold_expired_at: null

### 2-4. 예약 취소
```bash
curl -s -X DELETE -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/{예약ID}' | python3 -m json.tool
```
- [ ] status: SUCCESS
- [ ] 취소 후 재고가 복구되었는지 확인 (상세 조회로 remaining_quantity 확인)

### 2-5. 내 예약 목록 조회
```bash
curl -s -H 'X-Member-Id: 1' 'http://localhost:8080/api/v1/reservations' | python3 -m json.tool
```
- [ ] 위에서 생성/취소한 예약이 모두 보이는지
- [ ] 상태가 정확한지 (CANCELLED)

### 2-6. 재고 부족 시 예약 실패
```bash
# 모텔 201호는 재고 1개 — 이미 예약이 있으면 실패해야 함
# 먼저 예약 하나 생성 후, 같은 날짜로 다시 예약 시도
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/stay' \
  -d '{"reservation_request_id":"'$(uuidgen)'","accommodation_id":4,"room_option_id":8,"check_in_date":"2026-04-27","check_out_date":"2026-04-28","guest_name":"김철수","guest_phone":"010-0000-0000"}'

# 두 번째 예약 — 재고 소진
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 2' \
  'http://localhost:8080/api/v1/reservations/stay' \
  -d '{"reservation_request_id":"'$(uuidgen)'","accommodation_id":4,"room_option_id":8,"check_in_date":"2026-04-27","check_out_date":"2026-04-28","guest_name":"이영희","guest_phone":"010-1111-1111"}'
```
- [ ] 첫 번째: 201 SUCCESS
- [ ] 두 번째: INVENTORY_NOT_AVAILABLE 에러 (409)

### 2-7. 연박 예약 (2박)
```bash
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/stay' \
  -d '{"reservation_request_id":"'$(uuidgen)'","accommodation_id":5,"room_option_id":10,"check_in_date":"2026-04-28","check_out_date":"2026-04-30","guest_name":"박지민","guest_phone":"010-2222-2222"}'
```
- [ ] 2박 가격 합산: 150,000 + 150,000 = 300,000원 (VAT 포함 시 계산 확인)

---

## 3. 대실 예약 (모텔 전용)

### 3-1. 대실 예약 생성
> 사전 조건: 모텔 201호(옵션8)에 대실 시간 슬롯이 열려 있어야 함
```bash
# 시간 슬롯 열기 (파트너)
curl -s -X POST -H 'X-Partner-Id: 2' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/room-options/8/time-slots' \
  -d '{"date":"2026-04-28","start_time":"10:00","end_time":"22:00"}'

# 대실 예약
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/hourly' \
  -d '{"reservation_request_id":"'$(uuidgen)'","accommodation_id":4,"room_option_id":8,"date":"2026-04-28","start_time":"14:00","end_time":"18:00","guest_name":"대실고객","guest_phone":"010-3333-3333"}'
```
- [ ] reservation_type: HOURLY
- [ ] hourly_start_time: 14:00
- [ ] total_price가 대실 가격(30,000원)인지

---

## 4. 파트너 채널 (Extranet)

> 모든 요청에 `X-Partner-Id` 헤더 필수

### 4-1. 내 숙소 목록
```bash
curl -s -H 'X-Partner-Id: 1' 'http://localhost:8080/api/v1/extranet/accommodations' | python3 -m json.tool
```
- [ ] partner_id=1인 숙소만 나오는지 (호텔, 리조트)
- [ ] partner_id=2인 숙소는 안 나오는지

### 4-2. 숙소 등록 (다국어 포함)
```bash
curl -s -X POST -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/accommodations' \
  -d '{
    "name": "신규 테스트 호텔",
    "type": "HOTEL",
    "full_address": "서울시 중구 세종대로 1",
    "latitude": 37.5666, "longitude": 126.9784,
    "location_description": "시청역 1번 출구",
    "check_in_time": "15:00", "check_out_time": "11:00",
    "supported_locales": ["ko", "en"],
    "translations": [
      {"locale": "en", "name": "New Test Hotel", "full_address": "1 Sejong-daero, Jung-gu, Seoul"}
    ]
  }' | python3 -m json.tool
```
- [ ] status: PENDING (관리자 승인 필요)
- [ ] 다국어 데이터가 저장되었는지

### 4-3. 객실 등록
```bash
curl -s -X POST -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/accommodations/1/rooms' \
  -d '{"name":"테스트 객실","room_type":"FAMILY","standard_capacity":4,"max_capacity":6}'
```
- [ ] 객실 생성 확인

### 4-4. 객실 옵션 등록
```bash
curl -s -X POST -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/accommodations/1/rooms/1/options' \
  -d '{"name":"테스트 옵션","cancellation_policy":"PARTIAL_REFUND","additional_price":15000}'
```
- [ ] 옵션 생성 확인

### 4-5. 재고 설정
```bash
curl -s -X POST -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/room-options/1/inventories' \
  -d '{"start_date":"2026-05-15","end_date":"2026-05-20","total_quantity":5}' | python3 -m json.tool
```
- [ ] 5/15 ~ 5/20 재고 생성 (6일분)
- [ ] 각 날짜 total_quantity: 5, remaining_quantity: 5, status: AVAILABLE

### 4-6. 재고 조회
```bash
curl -s -H 'X-Partner-Id: 1' \
  'http://localhost:8080/api/v1/extranet/room-options/1/inventories?startDate=2026-05-15&endDate=2026-05-20'
```
- [ ] 위에서 설정한 재고가 보이는지

### 4-7. 요금 설정
```bash
curl -s -X POST -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/room-options/1/prices' \
  -d '{"start_date":"2026-05-15","end_date":"2026-05-20","price_type":"STAY","base_price":150000,"selling_price":130000,"tax_included":true}'
```
- [ ] 6일분 가격 생성

### 4-8. 요금 조회
```bash
curl -s -H 'X-Partner-Id: 1' \
  'http://localhost:8080/api/v1/extranet/room-options/1/prices?startDate=2026-05-15&endDate=2026-05-20'
```
- [ ] 가격 데이터 확인

### 4-9. 숙소 수정 (수정 요청 생성)
```bash
curl -s -X PUT -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/accommodations/1' \
  -d '{"name":"서울 그랜드 호텔 (리뉴얼)","full_address":"서울시 강남구 테헤란로 123","check_in_time":"14:00","check_out_time":"12:00"}'
```
- [ ] 수정 요청 생성됨 (즉시 반영 아님)

### 4-10. 파트너 예약 현황
```bash
curl -s -H 'X-Partner-Id: 1' \
  'http://localhost:8080/api/v1/extranet/reservations/accommodations/1' | python3 -m json.tool
```
- [ ] 해당 숙소의 예약 목록

### 4-11. 다른 파트너 숙소 접근 시도
```bash
# partner 1이 partner 2의 숙소(id:3) 객실 조회 시도
curl -s -H 'X-Partner-Id: 1' 'http://localhost:8080/api/v1/extranet/accommodations/3/rooms'
```
- [ ] **현재 동작**: 데이터가 나올 수 있음 (소유권 검증이 불완전할 수 있음)
- [ ] **기대 동작**: 403 또는 빈 배열 — 확인 필요

---

## 5. 관리자 채널 (Admin)

### 5-1. 전체 숙소 목록
```bash
curl -s 'http://localhost:8080/api/v1/admin/accommodations' | python3 -m json.tool
```
- [ ] 모든 숙소가 보이는지 (파트너 구분 없이)
- [ ] 상태 정보 표시

### 5-2. 숙소 상세
```bash
curl -s 'http://localhost:8080/api/v1/admin/accommodations/1' | python3 -m json.tool
```
- [ ] 상세 정보 + 이미지 목록

### 5-3. 숙소 승인 (PENDING → ACTIVE)
```bash
# 4-2에서 등록한 신규 숙소 ID로 테스트
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/accommodations/{신규숙소ID}/approve' | python3 -m json.tool
```
- [ ] status: ACTIVE로 변경

### 5-4. 숙소 정지 (ACTIVE → SUSPENDED)
```bash
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/accommodations/{숙소ID}/suspend' | python3 -m json.tool
```
- [ ] status: SUSPENDED

### 5-5. 숙소 폐쇄 (→ CLOSED)
```bash
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/accommodations/{숙소ID}/close' | python3 -m json.tool
```
- [ ] status: CLOSED

### 5-6. 잘못된 상태 전환 시도
```bash
# 이미 ACTIVE인 숙소를 다시 approve
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/accommodations/1/approve'
```
- [ ] 에러 응답 (이미 ACTIVE)

### 5-7. 수정 요청 목록
```bash
curl -s 'http://localhost:8080/api/v1/admin/accommodations/modifications/pending' | python3 -m json.tool
```
- [ ] 4-9에서 생성한 수정 요청이 보이는지

### 5-8. 수정 요청 승인
```bash
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/accommodations/modifications/{수정요청ID}/approve'
```
- [ ] 승인 후 숙소 데이터가 실제로 변경되었는지 확인

### 5-9. 수정 요청 거절
```bash
curl -s -X PATCH -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/admin/accommodations/modifications/{ID}/reject' \
  -d '{"reason": "정보 불충분"}'
```
- [ ] 거절 처리

### 5-10. 태그 그룹 관리
```bash
# 조회
curl -s 'http://localhost:8080/api/v1/admin/tag-groups' | python3 -m json.tool

# 생성
curl -s -X POST -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/admin/tag-groups' \
  -d '{"name":"테스트 그룹","display_order":10,"target_type":"ACCOMMODATION","is_active":true}'

# 비활성화
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/tag-groups/{그룹ID}/deactivate'

# 활성화
curl -s -X PATCH 'http://localhost:8080/api/v1/admin/tag-groups/{그룹ID}/activate'
```
- [ ] CRUD + 활성/비활성 전환

### 5-11. 태그 관리
```bash
# 태그 추가
curl -s -X POST -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/admin/tag-groups/1/tags' \
  -d '{"name":"테스트태그","display_order":10,"is_active":true}'

# 태그 목록
curl -s 'http://localhost:8080/api/v1/admin/tag-groups/1/tags'
```
- [ ] 태그 생성/조회

### 5-12. 관리자 가격 조정
```bash
curl -s -X PATCH -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/admin/room-options/1/price' \
  -d '{"start_date":"2026-04-25","end_date":"2026-04-26","selling_price":100000,"tax_included":true}'
```
- [ ] 가격이 변경되었는지 (120,000 → 100,000)
- [ ] 검색 결과에서 변경된 가격 반영 확인

---

## 6. 보안/인가 검증 (현재 미구현 — 확인 필요 항목)

> **현재 SecurityConfig**: `anyRequest().permitAll()` — JWT 미구현

### 6-1. 관리자 API에 인증 없이 접근
```bash
curl -s 'http://localhost:8080/api/v1/admin/accommodations'
```
- [ ] **현재**: 200 OK (모두 허용됨)
- [ ] **TODO**: JWT + ADMIN Role 필요하도록 변경해야 함

### 6-2. 파트너 API에 X-Partner-Id 없이 접근
```bash
curl -s 'http://localhost:8080/api/v1/extranet/accommodations'
```
- [ ] 400 에러 (Required header missing) — 이건 동작함

### 6-3. 다른 파트너 데이터 접근
```bash
# Partner 1이 Partner 2의 숙소(3번 게스트하우스) 수정 시도
curl -s -X PUT -H 'X-Partner-Id: 1' -H 'Content-Type: application/json' \
  'http://localhost:8080/api/v1/extranet/accommodations/3' \
  -d '{"name":"해킹 시도"}'
```
- [ ] 거부되는지 확인 (소유권 검증)

### 6-4. 다른 회원 예약 접근
```bash
# Member 2가 Member 1의 예약 조회 시도
curl -s -H 'X-Member-Id: 2' 'http://localhost:8080/api/v1/reservations/{member1의예약ID}'
```
- [ ] 거부되는지 확인

---

## 7. 에러 처리 검증

### 7-1. 필수 필드 누락
```bash
curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: 1' \
  'http://localhost:8080/api/v1/reservations/stay' \
  -d '{"accommodation_id": 1}'
```
- [ ] VALIDATION_ERROR + field_errors 배열

### 7-2. 존재하지 않는 리소스
```bash
curl -s 'http://localhost:8080/api/v1/accommodations/99999?checkInDate=2026-04-25&checkOutDate=2026-04-26'
```
- [ ] NOT_FOUND 에러

### 7-3. 잘못된 날짜 형식
```bash
curl -s 'http://localhost:8080/api/v1/accommodations?checkInDate=not-a-date&checkOutDate=2026-04-26&guestCount=2'
```
- [ ] 400 에러 (파싱 실패)

---

## 8. 동시성 테스트

> 터미널에서 직접 실행하거나 테스트 코드로 검증

### 8-1. 동시 예약 — 재고 1개에 5명 동시 요청
```bash
for i in {1..5}; do
  curl -s -X POST -H 'Content-Type: application/json' -H 'X-Member-Id: '$i \
    'http://localhost:8080/api/v1/reservations/stay' \
    -d '{"reservation_request_id":"'$(uuidgen)'","accommodation_id":4,"room_option_id":9,"check_in_date":"2026-04-29","check_out_date":"2026-04-30","guest_name":"동시테스트'$i'","guest_phone":"010-000-'$i'"}' &
done
wait
```
- [ ] 정확히 1명만 성공 (모텔 202호 재고 1개)
- [ ] 나머지 4명은 INVENTORY_NOT_AVAILABLE

---

## 9. Supplier 연동

```bash
curl -s -X POST 'http://localhost:8080/api/v1/supplier/MINHYUK_HOUSE/sync?startDate=2026-05-01&endDate=2026-05-07' | python3 -m json.tool
```
- [ ] 동기화 결과 확인 (Mock 공급사이므로 결과 형태만 확인)

---

## 검증 결과 요약

| 카테고리 | 항목 수 | 통과 | 실패 | 비고 |
|---------|--------|------|------|------|
| 고객 검색/상세 | 7 | | | |
| 고객 예약 | 7 | | | |
| 대실 예약 | 1 | | | 모텔 전용 |
| 파트너 API | 11 | | | |
| 관리자 API | 12 | | | |
| 보안/인가 | 4 | | | 일부 TODO |
| 에러 처리 | 3 | | | |
| 동시성 | 1 | | | |
| Supplier | 1 | | | |
| **합계** | **47** | | | |
