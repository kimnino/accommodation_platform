# Phase 8: API 전수 테스트 체크리스트

## Extranet (파트너) API

### 숙소
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E1 | 숙소 등록 | POST | /api/v1/extranet/accommodations | partner-acc.html | |
| E2 | 숙소 수정 요청 | PUT | /api/v1/extranet/accommodations/{id} | partner-acc.html | |
| E3 | 숙소 목록 조회 | GET | /api/v1/extranet/accommodations | partner-acc.html | |
| E4 | 숙소 상세 조회 | GET | /api/v1/extranet/accommodations/{id} | partner-acc.html | |

### 객실
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E5 | 객실 등록 | POST | /api/v1/extranet/accommodations/{id}/rooms | partner-rooms.html | |
| E6 | 객실 수정 | PUT | /api/v1/extranet/accommodations/{id}/rooms/{roomId} | partner-rooms.html | |
| E7 | 객실 삭제 | DELETE | /api/v1/extranet/accommodations/{id}/rooms/{roomId} | partner-rooms.html | |
| E8 | 객실 목록 조회 | GET | /api/v1/extranet/accommodations/{id}/rooms | partner-rooms.html | |
| E9 | 객실 상세 조회 | GET | /api/v1/extranet/accommodations/{id}/rooms/{roomId} | partner-rooms.html | |

### 객실 옵션
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E10 | 옵션 등록 | POST | /api/v1/extranet/accommodations/{id}/rooms/{roomId}/options | partner-rooms.html | |
| E11 | 옵션 수정 | PUT | /api/v1/extranet/accommodations/{id}/rooms/{roomId}/options/{optionId} | partner-rooms.html | |
| E12 | 옵션 삭제 | DELETE | /api/v1/extranet/accommodations/{id}/rooms/{roomId}/options/{optionId} | partner-rooms.html | |
| E13 | 옵션 목록 조회 | GET | /api/v1/extranet/accommodations/{id}/rooms/{roomId}/options | partner-rooms.html | |

### 재고
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E14 | 재고 설정 | POST | /api/v1/extranet/room-options/{id}/inventories | partner-setup.html | |
| E15 | 재고 조회 | GET | /api/v1/extranet/room-options/{id}/inventories | partner-setup.html | |

### 가격
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E16 | 가격 설정 | POST | /api/v1/extranet/room-options/{id}/prices | partner-setup.html | |
| E17 | 가격 조회 | GET | /api/v1/extranet/room-options/{id}/prices | partner-setup.html | |

### 대실
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E18 | 대실 설정 저장 | PUT | /api/v1/extranet/accommodations/{id}/hourly-setting | partner-hourly.html | |
| E19 | 대실 슬롯 오픈 | POST | /api/v1/extranet/room-options/{id}/time-slots | partner-hourly.html | |

### 태그
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E20 | 사용 가능 태그 그룹 조회 | GET | /api/v1/extranet/accommodations/{id}/tags/groups | partner-tags.html | |
| E21 | 태그 목록 조회 | GET | /api/v1/extranet/accommodations/{id}/tags/groups/{groupId} | partner-tags.html | |
| E22 | 숙소 태그 조회 | GET | /api/v1/extranet/accommodations/{id}/tags | partner-tags.html | |
| E23 | 숙소 태그 추가 | POST | /api/v1/extranet/accommodations/{id}/tags | partner-tags.html | |
| E24 | 숙소 태그 삭제 | DELETE | /api/v1/extranet/accommodations/{id}/tags | partner-tags.html | |

### 예약
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| E25 | 예약 확정 | PATCH | /api/v1/extranet/reservations/{id}/confirm | partner-res.html | |
| E26 | 예약 취소 | PATCH | /api/v1/extranet/reservations/{id}/cancel | partner-res.html | |
| E27 | 숙소별 예약 목록 | GET | /api/v1/extranet/reservations/accommodations/{id} | partner-res.html | |

---

## Admin (관리자) API

### 숙소
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| A1 | 숙소 목록 | GET | /api/v1/admin/accommodations | admin-acc.html | |
| A2 | 숙소 상세 | GET | /api/v1/admin/accommodations/{id} | admin-acc.html | |
| A3 | 숙소 승인 | PATCH | /api/v1/admin/accommodations/{id}/approve | admin-acc.html | |
| A4 | 숙소 정지 | PATCH | /api/v1/admin/accommodations/{id}/suspend | admin-acc.html | |
| A5 | 숙소 폐쇄 | PATCH | /api/v1/admin/accommodations/{id}/close | admin-acc.html | |

### 수정 요청
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| A6 | 수정 요청 목록 | GET | /api/v1/admin/accommodations/modifications/pending | admin-acc.html | |
| A7 | 수정 요청 승인 | PATCH | /api/v1/admin/accommodations/modifications/{id}/approve | admin-acc.html | |
| A8 | 수정 요청 거절 | PATCH | /api/v1/admin/accommodations/modifications/{id}/reject | admin-acc.html | |

### 가격
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| A9 | 가격 조정 | PATCH | /api/v1/admin/room-options/{id}/prices | admin-price.html | |

### 태그
| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| A10 | 태그 그룹 목록 | GET | /api/v1/admin/tag-groups | admin-tags.html | |
| A11 | 태그 그룹 생성 | POST | /api/v1/admin/tag-groups | admin-tags.html | |
| A12 | 태그 그룹 수정 | PUT | /api/v1/admin/tag-groups/{id} | admin-tags.html | |
| A13 | 태그 그룹 비활성화 | PATCH | /api/v1/admin/tag-groups/{id}/deactivate | admin-tags.html | |
| A14 | 태그 목록 | GET | /api/v1/admin/tag-groups/{id}/tags | admin-tags.html | |
| A15 | 태그 생성 | POST | /api/v1/admin/tag-groups/{id}/tags | admin-tags.html | |
| A16 | 태그 수정 | PUT | /api/v1/admin/tag-groups/{id}/tags/{tagId} | admin-tags.html | |
| A17 | 태그 비활성화 | PATCH | /api/v1/admin/tag-groups/{id}/tags/{tagId}/deactivate | admin-tags.html | |

---

## Customer (고객) API

| # | API | 메서드 | 경로 | HTML | 상태 |
|---|-----|--------|------|------|------|
| C1 | 숙소 검색 | GET | /api/v1/accommodations | customer.html | |
| C2 | 숙소 상세 | GET | /api/v1/accommodations/{id} | customer.html | |
| C3 | 숙박 예약 | POST | /api/v1/reservations/stay | customer.html | |
| C4 | 대실 예약 | POST | /api/v1/reservations/hourly | customer.html | |
| C5 | 예약 취소 | DELETE | /api/v1/reservations/{id} | customer.html | |
| C6 | 예약 상세 | GET | /api/v1/reservations/{id} | customer.html | |
| C7 | 내 예약 목록 | GET | /api/v1/reservations | customer.html | |

---

**총 44개 API**
