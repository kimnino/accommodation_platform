# Phase 8: 버그 리포트

> API 전수 테스트 후 발견된 버그/이슈를 아래에 작성해주세요. Claude가 읽고 수정합니다.

---

## Extranet (파트너) API

### E1. 숙소 등록 (POST /api/v1/extranet/accommodations)
> 

### E2. 숙소 수정 요청 (PUT /api/v1/extranet/accommodations/{id})
> 

### E3. 숙소 목록 조회 (GET /api/v1/extranet/accommodations)
> 

### E4. 숙소 상세 조회 (GET /api/v1/extranet/accommodations/{id})
> 

### E5. 객실 등록 (POST .../rooms)
> 

### E6. 객실 수정 (PUT .../rooms/{roomId})
> 

### E7. 객실 삭제 (DELETE .../rooms/{roomId})
> 

### E8. 객실 목록 조회 (GET .../rooms)
> 

### E9. 객실 상세 조회 (GET .../rooms/{roomId})
> 

### E10. 옵션 등록 (POST .../rooms/{roomId}/options)
> 

### E11. 옵션 수정 (PUT .../rooms/{roomId}/options/{optionId})
> 

### E12. 옵션 삭제 (DELETE .../rooms/{roomId}/options/{optionId})
> 

### E13. 옵션 목록 조회 (GET .../rooms/{roomId}/options)
> 

### E14. 재고 설정 (POST /api/v1/extranet/room-options/{id}/inventories)
> 

### E15. 재고 조회 (GET /api/v1/extranet/room-options/{id}/inventories)
> 

### E16. 가격 설정 (POST /api/v1/extranet/room-options/{id}/prices)
> 

### E17. 가격 조회 (GET /api/v1/extranet/room-options/{id}/prices)
> 

### E18. 대실 설정 저장 (PUT .../accommodations/{id}/hourly-setting)
> 

### E19. 대실 슬롯 오픈 (POST .../room-options/{id}/time-slots)
> 

### E20. 사용 가능 태그 그룹 조회 (GET .../tags/groups)
> 

### E21. 태그 목록 조회 (GET .../tags/groups/{groupId})
> 

### E22. 숙소 태그 조회 (GET .../tags)
> 

### E23. 숙소 태그 추가 (POST .../tags)
> 

### E24. 숙소 태그 삭제 (DELETE .../tags)
> 

### E25. 예약 확정 (PATCH .../reservations/{id}/confirm)
> 

### E26. 예약 취소 (PATCH .../reservations/{id}/cancel)
> 

### E27. 숙소별 예약 목록 (GET .../reservations/accommodations/{id})
> 

---

## Admin (관리자) API

### A1. 숙소 목록 (GET /api/v1/admin/accommodations)
> 

### A2. 숙소 상세 (GET /api/v1/admin/accommodations/{id})
> 

### A3. 숙소 승인 (PATCH .../accommodations/{id}/approve)
> 

### A4. 숙소 정지 (PATCH .../accommodations/{id}/suspend)
> 

### A5. 숙소 폐쇄 (PATCH .../accommodations/{id}/close)
> 

### A6. 수정 요청 목록 (GET .../modifications/pending)
> 

### A7. 수정 요청 승인 (PATCH .../modifications/{id}/approve)
> 

### A8. 수정 요청 거절 (PATCH .../modifications/{id}/reject)
> 

### A9. 가격 조정 (PATCH /api/v1/admin/room-options/{id}/prices)
> 

### A10. 태그 그룹 목록 (GET /api/v1/admin/tag-groups)
> 

### A11. 태그 그룹 생성 (POST /api/v1/admin/tag-groups)
> 

### A12. 태그 그룹 수정 (PUT /api/v1/admin/tag-groups/{id})
> 

### A13. 태그 그룹 비활성화 (PATCH .../tag-groups/{id}/deactivate)
> 

### A14. 태그 목록 (GET .../tag-groups/{id}/tags)
> 

### A15. 태그 생성 (POST .../tag-groups/{id}/tags)
> 

### A16. 태그 수정 (PUT .../tag-groups/{id}/tags/{tagId})
> 

### A17. 태그 비활성화 (PATCH .../tag-groups/{id}/tags/{tagId}/deactivate)
> 

---

## Customer (고객) API

### C1. 숙소 검색 (GET /api/v1/accommodations)
> 

### C2. 숙소 상세 (GET /api/v1/accommodations/{id})
> 

### C3. 숙박 예약 (POST /api/v1/reservations/stay)
> 

### C4. 대실 예약 (POST /api/v1/reservations/hourly)
> 

### C5. 예약 취소 (DELETE /api/v1/reservations/{id})
> 

### C6. 예약 상세 (GET /api/v1/reservations/{id})
> 

### C7. 내 예약 목록 (GET /api/v1/reservations)
> 

---

## HTML UI 버그

### 파트너 화면
> 

### 관리자 화면
> 

### 고객 화면
> 
