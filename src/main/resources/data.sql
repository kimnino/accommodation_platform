-- ============================================
-- 샘플 데이터 (local 프로필 전용)
-- ============================================

-- 숙소 등록 (파트너 ID: 1, 2)
INSERT INTO accommodation (id, partner_id, name, type, full_address, latitude, longitude, location_description, status, check_in_time, check_out_time, supplier_managed, created_at, updated_at)
VALUES (1, 1, '서울 그랜드 호텔', 'HOTEL', '서울시 강남구 테헤란로 123', 37.5665, 126.9780, '강남역 5번 출구 도보 3분', 'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
       (2, 1, '해운대 리조트', 'RESORT', '부산시 해운대구 해운대로 456', 35.1587, 129.1604, '해운대 해수욕장 앞', 'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
       (3, 2, '홍대 게스트하우스', 'GUEST_HOUSE', '서울시 마포구 와우산로 789', 37.5563, 126.9236, '홍대입구역 9번 출구', 'PENDING', '16:00', '10:00', false, NOW(), NOW()),
       (4, 2, '역삼 모텔', 'MOTEL', '서울시 강남구 역삼동 101', 37.5000, 127.0365, '역삼역 3번 출구', 'PENDING', '21:00', '11:00', false, NOW(), NOW());

-- 숙소 이미지
INSERT INTO accommodation_image (id, accommodation_id, relative_path, category, display_order, is_primary)
VALUES (1, 1, '/accommodation/hotel/exterior_main.png', 'EXTERIOR', 1, true),
       (2, 1, '/accommodation/hotel/lobby.png', 'LOBBY', 2, false),
       (3, 2, '/accommodation/resort/exterior_main.png', 'EXTERIOR', 1, true),
       (4, 3, '/accommodation/guesthouse/exterior_main.png', 'EXTERIOR', 1, true),
       (5, 4, '/accommodation/motel/exterior_main.png', 'EXTERIOR', 1, true);

-- 객실
INSERT INTO room (id, accommodation_id, name, room_type_name, standard_capacity, max_capacity, status, created_at, updated_at)
VALUES (1, 1, '디럭스 더블', '디럭스', 2, 4, 'ACTIVE', NOW(), NOW()),
       (2, 1, '스위트룸', '스위트', 2, 4, 'ACTIVE', NOW(), NOW()),
       (3, 2, '오션뷰 디럭스', '디럭스', 2, 3, 'ACTIVE', NOW(), NOW()),
       (4, 3, '4인 도미토리', NULL, 1, 4, 'ACTIVE', NOW(), NOW()),
       (5, 3, '개인실', NULL, 1, 2, 'ACTIVE', NOW(), NOW()),
       (6, 4, '201호', NULL, 2, 2, 'ACTIVE', NOW(), NOW()),
       (7, 4, '202호', NULL, 2, 2, 'ACTIVE', NOW(), NOW());

-- 객실 옵션
INSERT INTO room_option (id, room_id, name, cancellation_policy, additional_price, created_at, updated_at)
VALUES (1, 1, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
       (2, 1, '조식 포함', 'FREE_CANCELLATION', 20000, NOW(), NOW()),
       (3, 2, '기본', 'NON_REFUNDABLE', 0, NOW(), NOW()),
       (4, 3, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
       (5, 4, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
       (6, 5, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
       (7, 6, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
       (8, 7, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW());

-- 재고 (2026-04-25 ~ 2026-04-30, 각 옵션당 3개)
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at)
VALUES
(1,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-04-28',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),
(3,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-04-25',5,5,'AVAILABLE',NOW(),NOW()),(4,'2026-04-26',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-04-25',4,4,'AVAILABLE',NOW(),NOW()),(5,'2026-04-26',4,4,'AVAILABLE',NOW(),NOW()),
(6,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),(6,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),
(7,'2026-04-25',1,1,'AVAILABLE',NOW(),NOW()),(7,'2026-04-26',1,1,'AVAILABLE',NOW(),NOW()),
(8,'2026-04-25',1,1,'AVAILABLE',NOW(),NOW()),(8,'2026-04-26',1,1,'AVAILABLE',NOW(),NOW());

-- 숙박 가격
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at)
VALUES
(1,'2026-04-25','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-04-26','STAY',150000,120000,true,NOW(),NOW()),
(2,'2026-04-25','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-04-26','STAY',170000,140000,true,NOW(),NOW()),
(3,'2026-04-25','STAY',300000,250000,true,NOW(),NOW()),
(3,'2026-04-26','STAY',300000,250000,true,NOW(),NOW()),
(4,'2026-04-25','STAY',200000,180000,true,NOW(),NOW()),
(4,'2026-04-26','STAY',250000,220000,true,NOW(),NOW()),
(5,'2026-04-25','STAY',30000,25000,true,NOW(),NOW()),
(5,'2026-04-26','STAY',30000,25000,true,NOW(),NOW()),
(6,'2026-04-25','STAY',60000,50000,true,NOW(),NOW()),
(6,'2026-04-26','STAY',60000,50000,true,NOW(),NOW()),
(7,'2026-04-25','STAY',80000,60000,true,NOW(),NOW()),
(7,'2026-04-26','STAY',80000,60000,true,NOW(),NOW()),
(8,'2026-04-25','STAY',80000,60000,true,NOW(),NOW()),
(7,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW()),
(7,'2026-04-26','HOURLY',40000,30000,true,NOW(),NOW()),
(8,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW());

-- 태그 그룹
INSERT INTO tag_group (id, name, display_order, target_type, accommodation_type, is_active, created_at, updated_at)
VALUES (1, '공용시설', 1, 'ACCOMMODATION', NULL, true, NOW(), NOW()),
       (2, '서비스', 2, 'ACCOMMODATION', 'HOTEL', true, NOW(), NOW());

-- 태그
INSERT INTO tag (id, tag_group_id, name, display_order, is_active, created_at, updated_at)
VALUES (1, 1, '수영장', 1, true, NOW(), NOW()),
       (2, 1, '사우나', 2, true, NOW(), NOW()),
       (3, 1, '피트니스', 3, true, NOW(), NOW()),
       (4, 2, '룸서비스', 1, true, NOW(), NOW()),
       (5, 2, '발렛파킹', 2, true, NOW(), NOW());

-- 숙소 태그
INSERT INTO accommodation_tag (accommodation_id, tag_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
       (2, 1), (2, 3);
