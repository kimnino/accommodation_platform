-- ============================================
-- 샘플 데이터
-- ============================================

-- 숙소 (유형별 1개씩, 파트너 3명 + 승인 대기 2개)
INSERT INTO accommodation (id, partner_id, name, type, full_address, latitude, longitude, location_description, status, check_in_time, check_out_time, supplier_managed, created_at, updated_at)
VALUES (1, 1, '서울 그랜드 호텔', 'HOTEL', '서울시 강남구 테헤란로 123', 37.5665, 126.9780, '강남역 5번 출구 도보 3분', 'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
       (2, 1, '해운대 리조트', 'RESORT', '부산시 해운대구 해운대로 456', 35.1587, 129.1604, '해운대 해수욕장 앞', 'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
       (3, 2, '홍대 게스트하우스', 'GUEST_HOUSE', '서울시 마포구 와우산로 789', 37.5563, 126.9236, '홍대입구역 9번 출구', 'ACTIVE', '16:00', '10:00', false, NOW(), NOW()),
       (4, 2, '역삼 모텔', 'MOTEL', '서울시 강남구 역삼동 101', 37.5000, 127.0365, '역삼역 3번 출구', 'ACTIVE', '21:00', '11:00', false, NOW(), NOW()),
       (5, 3, '가평 숲속 펜션', 'PENSION', '경기도 가평군 북면 백둔로 55', 37.8316, 127.5106, '자라섬 차량 10분', 'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
       (6, 3, '제주 오션 풀빌라', 'POOL_VILLA', '제주시 애월읍 해안로 200', 33.4630, 126.3200, '애월 해안도로 위치', 'ACTIVE', '15:00', '11:00', false, NOW(), NOW());

-- 숙소 이미지
INSERT INTO accommodation_image (id, accommodation_id, relative_path, category, display_order, is_primary)
VALUES (1, 1, '/accommodation/hotel/exterior_main.png', 'EXTERIOR', 1, true),
       (2, 1, '/accommodation/hotel/lobby.png', 'LOBBY', 2, false),
       (3, 2, '/accommodation/resort/exterior_main.png', 'EXTERIOR', 1, true),
       (4, 3, '/accommodation/guesthouse/exterior_main.png', 'EXTERIOR', 1, true),
       (5, 4, '/accommodation/motel/exterior_main.png', 'EXTERIOR', 1, true),
       (6, 5, '/accommodation/pension/exterior_main.png', 'EXTERIOR', 1, true),
       (7, 6, '/accommodation/poolvilla/exterior_main.png', 'EXTERIOR', 1, true);

-- ============================================
-- 객실 & 옵션
-- 호텔: 2객실 × 2옵션 / 나머지: 1객실 × 1옵션 / 모텔: 2객실 × 1옵션
-- ============================================

INSERT INTO room (id, accommodation_id, name, room_type_name, standard_capacity, max_capacity, status, created_at, updated_at)
VALUES
(1, 1, '디럭스 더블', '디럭스', 2, 4, 'ACTIVE', NOW(), NOW()),
(2, 1, '스위트룸', '스위트', 2, 4, 'ACTIVE', NOW(), NOW()),
(3, 2, '오션뷰 디럭스', '디럭스', 2, 3, 'ACTIVE', NOW(), NOW()),
(4, 3, '4인 도미토리', '도미토리', 1, 4, 'ACTIVE', NOW(), NOW()),
(5, 4, '201호', '스탠다드', 2, 2, 'ACTIVE', NOW(), NOW()),
(6, 4, '202호', '스탠다드', 2, 2, 'ACTIVE', NOW(), NOW()),
(7, 5, '숲속 A동', '패밀리', 4, 6, 'ACTIVE', NOW(), NOW()),
(8, 6, '오션뷰 빌라', '빌라', 4, 8, 'ACTIVE', NOW(), NOW());

INSERT INTO room_option (id, room_id, name, cancellation_policy, additional_price, created_at, updated_at)
VALUES
-- 호텔 디럭스: 기본 + 조식
(1, 1, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(2, 1, '조식 포함', 'FREE_CANCELLATION', 20000, NOW(), NOW()),
-- 호텔 스위트: 기본 + 조식+스파
(3, 2, '기본', 'NON_REFUNDABLE', 0, NOW(), NOW()),
(4, 2, '조식+스파 패키지', 'PARTIAL_REFUND', 80000, NOW(), NOW()),
-- 리조트: 기본
(5, 3, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
-- 게스트하우스: 기본
(6, 4, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
-- 모텔 201호: 기본
(7, 5, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
-- 모텔 202호: 기본
(8, 6, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
-- 펜션: 기본
(9, 7, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
-- 풀빌라: 기본
(10, 8, '기본', 'PARTIAL_REFUND', 0, NOW(), NOW());

-- ============================================
-- 재고 (2026-04-22 ~ 2026-05-10)
-- ============================================

-- 호텔 디럭스 기본 (옵션1): 3실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(1,'2026-04-22',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-04-23',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-04-24',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-04-28',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-04-29',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-04-30',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-05-01',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-05-02',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-05-03',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-05-04',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-05-05',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-05-06',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-05-07',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-05-08',3,3,'AVAILABLE',NOW(),NOW()),(1,'2026-05-09',3,3,'AVAILABLE',NOW(),NOW()),
(1,'2026-05-10',3,3,'AVAILABLE',NOW(),NOW());

-- 호텔 디럭스 조식 (옵션2): 3실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(2,'2026-04-22',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-04-23',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-04-24',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-04-28',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-04-29',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-04-30',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-05-01',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-05-02',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-05-03',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-05-04',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-05-05',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-05-06',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-05-07',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-05-08',3,3,'AVAILABLE',NOW(),NOW()),(2,'2026-05-09',3,3,'AVAILABLE',NOW(),NOW()),
(2,'2026-05-10',3,3,'AVAILABLE',NOW(),NOW());

-- 호텔 스위트 기본 (옵션3): 2실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(3,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),
(3,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),
(3,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),
(3,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),
(3,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),
(3,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),
(3,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(3,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW());

-- 호텔 스위트 조식+스파 (옵션4): 2실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(4,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),
(4,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(4,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW());

-- 리조트 (옵션5): 5실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(5,'2026-04-22',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-04-23',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-04-24',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-04-25',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-04-26',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-04-27',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-04-28',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-04-29',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-04-30',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-05-01',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-05-02',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-05-03',5,5,'AVAILABLE',NOW(),NOW()),
(5,'2026-05-04',5,5,'AVAILABLE',NOW(),NOW()),(5,'2026-05-05',5,5,'AVAILABLE',NOW(),NOW());

-- 게스트하우스 (옵션6): 4베드
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(6,'2026-04-22',4,4,'AVAILABLE',NOW(),NOW()),(6,'2026-04-23',4,4,'AVAILABLE',NOW(),NOW()),
(6,'2026-04-24',4,4,'AVAILABLE',NOW(),NOW()),(6,'2026-04-25',4,4,'AVAILABLE',NOW(),NOW()),
(6,'2026-04-26',4,4,'AVAILABLE',NOW(),NOW()),(6,'2026-04-27',4,4,'AVAILABLE',NOW(),NOW()),
(6,'2026-04-28',4,4,'AVAILABLE',NOW(),NOW()),(6,'2026-04-29',4,4,'AVAILABLE',NOW(),NOW()),
(6,'2026-04-30',4,4,'AVAILABLE',NOW(),NOW()),(6,'2026-05-01',4,4,'AVAILABLE',NOW(),NOW()),
(6,'2026-05-02',4,4,'AVAILABLE',NOW(),NOW()),(6,'2026-05-03',4,4,'AVAILABLE',NOW(),NOW());

-- 모텔 201호 (옵션7): 1실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(7,'2026-04-22',1,1,'AVAILABLE',NOW(),NOW()),(7,'2026-04-23',1,1,'AVAILABLE',NOW(),NOW()),
(7,'2026-04-24',1,1,'AVAILABLE',NOW(),NOW()),(7,'2026-04-25',1,1,'AVAILABLE',NOW(),NOW()),
(7,'2026-04-26',1,1,'AVAILABLE',NOW(),NOW()),(7,'2026-04-27',1,1,'AVAILABLE',NOW(),NOW()),
(7,'2026-04-28',1,1,'AVAILABLE',NOW(),NOW()),(7,'2026-04-29',1,1,'AVAILABLE',NOW(),NOW()),
(7,'2026-04-30',1,1,'AVAILABLE',NOW(),NOW()),(7,'2026-05-01',1,1,'AVAILABLE',NOW(),NOW()),
(7,'2026-05-02',1,1,'AVAILABLE',NOW(),NOW());

-- 모텔 202호 (옵션8): 1실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(8,'2026-04-22',1,1,'AVAILABLE',NOW(),NOW()),(8,'2026-04-23',1,1,'AVAILABLE',NOW(),NOW()),
(8,'2026-04-24',1,1,'AVAILABLE',NOW(),NOW()),(8,'2026-04-25',1,1,'AVAILABLE',NOW(),NOW()),
(8,'2026-04-26',1,1,'AVAILABLE',NOW(),NOW()),(8,'2026-04-27',1,1,'AVAILABLE',NOW(),NOW()),
(8,'2026-04-28',1,1,'AVAILABLE',NOW(),NOW()),(8,'2026-04-29',1,1,'AVAILABLE',NOW(),NOW()),
(8,'2026-04-30',1,1,'AVAILABLE',NOW(),NOW()),(8,'2026-05-01',1,1,'AVAILABLE',NOW(),NOW()),
(8,'2026-05-02',1,1,'AVAILABLE',NOW(),NOW());

-- 펜션 (옵션9): 2실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(9,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(9,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),
(9,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(9,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),
(9,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(9,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),
(9,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(9,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),
(9,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(9,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),
(9,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW());

-- 풀빌라 (옵션10): 1실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(10,'2026-04-22',1,1,'AVAILABLE',NOW(),NOW()),(10,'2026-04-23',1,1,'AVAILABLE',NOW(),NOW()),
(10,'2026-04-24',1,1,'AVAILABLE',NOW(),NOW()),(10,'2026-04-25',1,1,'AVAILABLE',NOW(),NOW()),
(10,'2026-04-26',1,1,'AVAILABLE',NOW(),NOW()),(10,'2026-04-27',1,1,'AVAILABLE',NOW(),NOW()),
(10,'2026-04-28',1,1,'AVAILABLE',NOW(),NOW()),(10,'2026-04-29',1,1,'AVAILABLE',NOW(),NOW()),
(10,'2026-04-30',1,1,'AVAILABLE',NOW(),NOW()),(10,'2026-05-01',1,1,'AVAILABLE',NOW(),NOW()),
(10,'2026-05-02',1,1,'AVAILABLE',NOW(),NOW());

-- ============================================
-- 요금 (평일/주말 구분)
-- ============================================

-- 호텔 디럭스 기본 (옵션1): 평일 120,000 / 주말 150,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(1,'2026-04-22','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-04-23','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-04-24','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-04-25','STAY',180000,150000,true,NOW(),NOW()),
(1,'2026-04-26','STAY',180000,150000,true,NOW(),NOW()),(1,'2026-04-27','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-04-28','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-04-29','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-04-30','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-05-01','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-05-02','STAY',180000,150000,true,NOW(),NOW()),(1,'2026-05-03','STAY',180000,150000,true,NOW(),NOW()),
(1,'2026-05-04','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-05-05','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-05-06','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-05-07','STAY',150000,120000,true,NOW(),NOW()),
(1,'2026-05-08','STAY',150000,120000,true,NOW(),NOW()),(1,'2026-05-09','STAY',180000,150000,true,NOW(),NOW()),
(1,'2026-05-10','STAY',180000,150000,true,NOW(),NOW());

-- 호텔 디럭스 조식 (옵션2): 평일 140,000 / 주말 170,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(2,'2026-04-22','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-04-23','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-04-24','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-04-25','STAY',200000,170000,true,NOW(),NOW()),
(2,'2026-04-26','STAY',200000,170000,true,NOW(),NOW()),(2,'2026-04-27','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-04-28','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-04-29','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-04-30','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-05-01','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-05-02','STAY',200000,170000,true,NOW(),NOW()),(2,'2026-05-03','STAY',200000,170000,true,NOW(),NOW()),
(2,'2026-05-04','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-05-05','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-05-06','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-05-07','STAY',170000,140000,true,NOW(),NOW()),
(2,'2026-05-08','STAY',170000,140000,true,NOW(),NOW()),(2,'2026-05-09','STAY',200000,170000,true,NOW(),NOW()),
(2,'2026-05-10','STAY',200000,170000,true,NOW(),NOW());

-- 호텔 스위트 기본 (옵션3): 평일 250,000 / 주말 300,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(3,'2026-04-22','STAY',300000,250000,true,NOW(),NOW()),(3,'2026-04-23','STAY',300000,250000,true,NOW(),NOW()),
(3,'2026-04-24','STAY',300000,250000,true,NOW(),NOW()),(3,'2026-04-25','STAY',350000,300000,true,NOW(),NOW()),
(3,'2026-04-26','STAY',350000,300000,true,NOW(),NOW()),(3,'2026-04-27','STAY',300000,250000,true,NOW(),NOW()),
(3,'2026-04-28','STAY',300000,250000,true,NOW(),NOW()),(3,'2026-04-29','STAY',300000,250000,true,NOW(),NOW()),
(3,'2026-04-30','STAY',300000,250000,true,NOW(),NOW()),(3,'2026-05-01','STAY',300000,250000,true,NOW(),NOW()),
(3,'2026-05-02','STAY',350000,300000,true,NOW(),NOW()),(3,'2026-05-03','STAY',350000,300000,true,NOW(),NOW()),
(3,'2026-05-04','STAY',300000,250000,true,NOW(),NOW()),(3,'2026-05-05','STAY',300000,250000,true,NOW(),NOW());

-- 호텔 스위트 조식+스파 (옵션4): 평일 330,000 / 주말 380,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(4,'2026-04-22','STAY',380000,330000,true,NOW(),NOW()),(4,'2026-04-23','STAY',380000,330000,true,NOW(),NOW()),
(4,'2026-04-24','STAY',380000,330000,true,NOW(),NOW()),(4,'2026-04-25','STAY',430000,380000,true,NOW(),NOW()),
(4,'2026-04-26','STAY',430000,380000,true,NOW(),NOW()),(4,'2026-04-27','STAY',380000,330000,true,NOW(),NOW()),
(4,'2026-04-28','STAY',380000,330000,true,NOW(),NOW()),(4,'2026-04-29','STAY',380000,330000,true,NOW(),NOW()),
(4,'2026-04-30','STAY',380000,330000,true,NOW(),NOW()),(4,'2026-05-01','STAY',380000,330000,true,NOW(),NOW()),
(4,'2026-05-02','STAY',430000,380000,true,NOW(),NOW()),(4,'2026-05-03','STAY',430000,380000,true,NOW(),NOW()),
(4,'2026-05-04','STAY',380000,330000,true,NOW(),NOW()),(4,'2026-05-05','STAY',380000,330000,true,NOW(),NOW());

-- 리조트 (옵션5): 평일 180,000 / 주말 220,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(5,'2026-04-22','STAY',200000,180000,true,NOW(),NOW()),(5,'2026-04-23','STAY',200000,180000,true,NOW(),NOW()),
(5,'2026-04-24','STAY',200000,180000,true,NOW(),NOW()),(5,'2026-04-25','STAY',250000,220000,true,NOW(),NOW()),
(5,'2026-04-26','STAY',250000,220000,true,NOW(),NOW()),(5,'2026-04-27','STAY',200000,180000,true,NOW(),NOW()),
(5,'2026-04-28','STAY',200000,180000,true,NOW(),NOW()),(5,'2026-04-29','STAY',200000,180000,true,NOW(),NOW()),
(5,'2026-04-30','STAY',200000,180000,true,NOW(),NOW()),(5,'2026-05-01','STAY',200000,180000,true,NOW(),NOW()),
(5,'2026-05-02','STAY',250000,220000,true,NOW(),NOW()),(5,'2026-05-03','STAY',250000,220000,true,NOW(),NOW()),
(5,'2026-05-04','STAY',200000,180000,true,NOW(),NOW()),(5,'2026-05-05','STAY',200000,180000,true,NOW(),NOW());

-- 게스트하우스 (옵션6): 25,000 (주말 30,000)
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(6,'2026-04-22','STAY',30000,25000,true,NOW(),NOW()),(6,'2026-04-23','STAY',30000,25000,true,NOW(),NOW()),
(6,'2026-04-24','STAY',30000,25000,true,NOW(),NOW()),(6,'2026-04-25','STAY',35000,30000,true,NOW(),NOW()),
(6,'2026-04-26','STAY',35000,30000,true,NOW(),NOW()),(6,'2026-04-27','STAY',30000,25000,true,NOW(),NOW()),
(6,'2026-04-28','STAY',30000,25000,true,NOW(),NOW()),(6,'2026-04-29','STAY',30000,25000,true,NOW(),NOW()),
(6,'2026-04-30','STAY',30000,25000,true,NOW(),NOW()),(6,'2026-05-01','STAY',30000,25000,true,NOW(),NOW()),
(6,'2026-05-02','STAY',35000,30000,true,NOW(),NOW()),(6,'2026-05-03','STAY',35000,30000,true,NOW(),NOW());

-- 모텔 201호 (옵션7): 숙박 60,000 / 대실 30,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(7,'2026-04-22','STAY',80000,60000,true,NOW(),NOW()),(7,'2026-04-23','STAY',80000,60000,true,NOW(),NOW()),
(7,'2026-04-24','STAY',80000,60000,true,NOW(),NOW()),(7,'2026-04-25','STAY',90000,70000,true,NOW(),NOW()),
(7,'2026-04-26','STAY',90000,70000,true,NOW(),NOW()),(7,'2026-04-27','STAY',80000,60000,true,NOW(),NOW()),
(7,'2026-04-28','STAY',80000,60000,true,NOW(),NOW()),(7,'2026-04-29','STAY',80000,60000,true,NOW(),NOW()),
(7,'2026-04-30','STAY',80000,60000,true,NOW(),NOW()),(7,'2026-05-01','STAY',80000,60000,true,NOW(),NOW()),
(7,'2026-05-02','STAY',90000,70000,true,NOW(),NOW()),
(7,'2026-04-22','HOURLY',40000,30000,true,NOW(),NOW()),(7,'2026-04-23','HOURLY',40000,30000,true,NOW(),NOW()),
(7,'2026-04-24','HOURLY',40000,30000,true,NOW(),NOW()),(7,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW()),
(7,'2026-04-26','HOURLY',40000,30000,true,NOW(),NOW()),(7,'2026-04-27','HOURLY',40000,30000,true,NOW(),NOW()),
(7,'2026-04-28','HOURLY',40000,30000,true,NOW(),NOW()),(7,'2026-04-29','HOURLY',40000,30000,true,NOW(),NOW()),
(7,'2026-04-30','HOURLY',40000,30000,true,NOW(),NOW()),(7,'2026-05-01','HOURLY',40000,30000,true,NOW(),NOW()),
(7,'2026-05-02','HOURLY',40000,30000,true,NOW(),NOW());

-- 모텔 202호 (옵션8): 숙박 60,000 / 대실 30,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(8,'2026-04-22','STAY',80000,60000,true,NOW(),NOW()),(8,'2026-04-23','STAY',80000,60000,true,NOW(),NOW()),
(8,'2026-04-24','STAY',80000,60000,true,NOW(),NOW()),(8,'2026-04-25','STAY',90000,70000,true,NOW(),NOW()),
(8,'2026-04-26','STAY',90000,70000,true,NOW(),NOW()),(8,'2026-04-27','STAY',80000,60000,true,NOW(),NOW()),
(8,'2026-04-28','STAY',80000,60000,true,NOW(),NOW()),(8,'2026-04-29','STAY',80000,60000,true,NOW(),NOW()),
(8,'2026-04-30','STAY',80000,60000,true,NOW(),NOW()),(8,'2026-05-01','STAY',80000,60000,true,NOW(),NOW()),
(8,'2026-05-02','STAY',90000,70000,true,NOW(),NOW()),
(8,'2026-04-22','HOURLY',40000,30000,true,NOW(),NOW()),(8,'2026-04-23','HOURLY',40000,30000,true,NOW(),NOW()),
(8,'2026-04-24','HOURLY',40000,30000,true,NOW(),NOW()),(8,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW()),
(8,'2026-04-26','HOURLY',40000,30000,true,NOW(),NOW()),(8,'2026-04-27','HOURLY',40000,30000,true,NOW(),NOW()),
(8,'2026-04-28','HOURLY',40000,30000,true,NOW(),NOW()),(8,'2026-04-29','HOURLY',40000,30000,true,NOW(),NOW()),
(8,'2026-04-30','HOURLY',40000,30000,true,NOW(),NOW()),(8,'2026-05-01','HOURLY',40000,30000,true,NOW(),NOW()),
(8,'2026-05-02','HOURLY',40000,30000,true,NOW(),NOW());

-- 펜션 (옵션9): 평일 150,000 / 주말 190,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(9,'2026-04-22','STAY',180000,150000,true,NOW(),NOW()),(9,'2026-04-23','STAY',180000,150000,true,NOW(),NOW()),
(9,'2026-04-24','STAY',180000,150000,true,NOW(),NOW()),(9,'2026-04-25','STAY',220000,190000,true,NOW(),NOW()),
(9,'2026-04-26','STAY',220000,190000,true,NOW(),NOW()),(9,'2026-04-27','STAY',180000,150000,true,NOW(),NOW()),
(9,'2026-04-28','STAY',180000,150000,true,NOW(),NOW()),(9,'2026-04-29','STAY',180000,150000,true,NOW(),NOW()),
(9,'2026-04-30','STAY',180000,150000,true,NOW(),NOW()),(9,'2026-05-01','STAY',180000,150000,true,NOW(),NOW()),
(9,'2026-05-02','STAY',220000,190000,true,NOW(),NOW());

-- 풀빌라 (옵션10): 평일 350,000 / 주말 450,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(10,'2026-04-22','STAY',400000,350000,true,NOW(),NOW()),(10,'2026-04-23','STAY',400000,350000,true,NOW(),NOW()),
(10,'2026-04-24','STAY',400000,350000,true,NOW(),NOW()),(10,'2026-04-25','STAY',500000,450000,true,NOW(),NOW()),
(10,'2026-04-26','STAY',500000,450000,true,NOW(),NOW()),(10,'2026-04-27','STAY',400000,350000,true,NOW(),NOW()),
(10,'2026-04-28','STAY',400000,350000,true,NOW(),NOW()),(10,'2026-04-29','STAY',400000,350000,true,NOW(),NOW()),
(10,'2026-04-30','STAY',400000,350000,true,NOW(),NOW()),(10,'2026-05-01','STAY',400000,350000,true,NOW(),NOW()),
(10,'2026-05-02','STAY',500000,450000,true,NOW(),NOW());

-- ============================================
-- 태그 그룹 (유형별 + 공통)
-- ============================================
INSERT INTO tag_group (id, name, display_order, target_type, accommodation_type, is_active, created_at, updated_at)
VALUES
(1, '공용시설', 1, 'ACCOMMODATION', NULL, true, NOW(), NOW()),
(2, '호텔 서비스', 2, 'ACCOMMODATION', 'HOTEL', true, NOW(), NOW()),
(3, '리조트 특징', 3, 'ACCOMMODATION', 'RESORT', true, NOW(), NOW()),
(4, '모텔 편의', 4, 'ACCOMMODATION', 'MOTEL', true, NOW(), NOW()),
(5, '펜션 특징', 5, 'ACCOMMODATION', 'PENSION', true, NOW(), NOW()),
(6, '풀빌라 특징', 6, 'ACCOMMODATION', 'POOL_VILLA', true, NOW(), NOW()),
(7, '게스트하우스 특징', 7, 'ACCOMMODATION', 'GUEST_HOUSE', true, NOW(), NOW()),
(8, '객실 편의시설', 8, 'ROOM', NULL, true, NOW(), NOW());

-- 태그 (각 그룹 3개 이상)
INSERT INTO tag (id, tag_group_id, name, display_order, is_active, created_at, updated_at)
VALUES
-- 공용시설
(1, 1, '수영장', 1, true, NOW(), NOW()),
(2, 1, '사우나', 2, true, NOW(), NOW()),
(3, 1, '피트니스', 3, true, NOW(), NOW()),
(4, 1, '주차장', 4, true, NOW(), NOW()),
-- 호텔 서비스
(5, 2, '룸서비스', 1, true, NOW(), NOW()),
(6, 2, '발렛파킹', 2, true, NOW(), NOW()),
(7, 2, '컨시어지', 3, true, NOW(), NOW()),
(8, 2, '비즈니스센터', 4, true, NOW(), NOW()),
-- 리조트 특징
(9, 3, '워터파크', 1, true, NOW(), NOW()),
(10, 3, '키즈클럽', 2, true, NOW(), NOW()),
(11, 3, '골프장', 3, true, NOW(), NOW()),
-- 모텔 편의
(12, 4, 'OTT 구비', 1, true, NOW(), NOW()),
(13, 4, '무인 체크인', 2, true, NOW(), NOW()),
(14, 4, '주차 무료', 3, true, NOW(), NOW()),
-- 펜션 특징
(15, 5, 'BBQ', 1, true, NOW(), NOW()),
(16, 5, '계곡 인접', 2, true, NOW(), NOW()),
(17, 5, '반려동물 동반', 3, true, NOW(), NOW()),
(18, 5, '불멍', 4, true, NOW(), NOW()),
-- 풀빌라 특징
(19, 6, '개별 수영장', 1, true, NOW(), NOW()),
(20, 6, '오션뷰', 2, true, NOW(), NOW()),
(21, 6, '프라이빗 자쿠지', 3, true, NOW(), NOW()),
-- 게스트하우스 특징
(22, 7, '공용 주방', 1, true, NOW(), NOW()),
(23, 7, '라운지', 2, true, NOW(), NOW()),
(24, 7, '투어 프로그램', 3, true, NOW(), NOW()),
-- 객실 편의시설
(25, 8, '에어컨', 1, true, NOW(), NOW()),
(26, 8, 'TV', 2, true, NOW(), NOW()),
(27, 8, '미니바', 3, true, NOW(), NOW());

-- 숙소-태그 매핑
INSERT INTO accommodation_tag (accommodation_id, tag_id, created_at, updated_at)
VALUES
(1, 1, NOW(), NOW()), (1, 2, NOW(), NOW()), (1, 3, NOW(), NOW()), (1, 4, NOW(), NOW()), (1, 5, NOW(), NOW()), (1, 6, NOW(), NOW()), (1, 7, NOW(), NOW()), (1, 8, NOW(), NOW()),
(2, 1, NOW(), NOW()), (2, 3, NOW(), NOW()), (2, 4, NOW(), NOW()), (2, 9, NOW(), NOW()), (2, 10, NOW(), NOW()), (2, 11, NOW(), NOW()),
(3, 4, NOW(), NOW()), (3, 22, NOW(), NOW()), (3, 23, NOW(), NOW()), (3, 24, NOW(), NOW()),
(4, 4, NOW(), NOW()), (4, 12, NOW(), NOW()), (4, 13, NOW(), NOW()), (4, 14, NOW(), NOW()),
(5, 4, NOW(), NOW()), (5, 15, NOW(), NOW()), (5, 16, NOW(), NOW()), (5, 17, NOW(), NOW()), (5, 18, NOW(), NOW()),
(6, 1, NOW(), NOW()), (6, 4, NOW(), NOW()), (6, 19, NOW(), NOW()), (6, 20, NOW(), NOW()), (6, 21, NOW(), NOW());

-- ============================================
-- 숙소 유형별 지역 마스터
-- ============================================

-- HOTEL 지역
INSERT INTO accommodation_region (id, accommodation_type, region_name, parent_id, sort_order, created_at, updated_at) VALUES
(1,  'HOTEL', '서울',       NULL, 1, NOW(), NOW()),
(2,  'HOTEL', '강남구',     1,    1, NOW(), NOW()),
(3,  'HOTEL', '마포구',     1,    2, NOW(), NOW()),
(4,  'HOTEL', '중구',       1,    3, NOW(), NOW()),
(5,  'HOTEL', '종로구',     1,    4, NOW(), NOW()),
(6,  'HOTEL', '부산',       NULL, 2, NOW(), NOW()),
(7,  'HOTEL', '해운대구',   6,    1, NOW(), NOW()),
(8,  'HOTEL', '수영구',     6,    2, NOW(), NOW()),
(9,  'HOTEL', '제주',       NULL, 3, NOW(), NOW()),
(10, 'HOTEL', '제주시',     9,    1, NOW(), NOW()),
(11, 'HOTEL', '서귀포시',   9,    2, NOW(), NOW());

-- MOTEL 지역 (동 단위 세분화)
INSERT INTO accommodation_region (id, accommodation_type, region_name, parent_id, sort_order, created_at, updated_at) VALUES
(12, 'MOTEL', '서울',        NULL, 1, NOW(), NOW()),
(13, 'MOTEL', '잠실/송파',   12,   1, NOW(), NOW()),
(14, 'MOTEL', '강남/역삼',   12,   2, NOW(), NOW()),
(15, 'MOTEL', '홍대/마포',   12,   3, NOW(), NOW()),
(16, 'MOTEL', '천호/강동',   12,   4, NOW(), NOW()),
(17, 'MOTEL', '신림/관악',   12,   5, NOW(), NOW()),
(18, 'MOTEL', '부산',        NULL, 2, NOW(), NOW()),
(19, 'MOTEL', '서면',        18,   1, NOW(), NOW()),
(20, 'MOTEL', '해운대',      18,   2, NOW(), NOW()),
(21, 'MOTEL', '남포/중구',   18,   3, NOW(), NOW());

-- RESORT 지역
INSERT INTO accommodation_region (id, accommodation_type, region_name, parent_id, sort_order, created_at, updated_at) VALUES
(22, 'RESORT', '부산',      NULL, 1, NOW(), NOW()),
(23, 'RESORT', '해운대',    22,   1, NOW(), NOW()),
(24, 'RESORT', '제주',      NULL, 2, NOW(), NOW()),
(25, 'RESORT', '애월',      24,   1, NOW(), NOW()),
(26, 'RESORT', '서귀포',    24,   2, NOW(), NOW()),
(27, 'RESORT', '강원',      NULL, 3, NOW(), NOW()),
(28, 'RESORT', '속초',      27,   1, NOW(), NOW()),
(29, 'RESORT', '강릉',      27,   2, NOW(), NOW());

-- PENSION 지역
INSERT INTO accommodation_region (id, accommodation_type, region_name, parent_id, sort_order, created_at, updated_at) VALUES
(30, 'PENSION', '경기',     NULL, 1, NOW(), NOW()),
(31, 'PENSION', '가평',     30,   1, NOW(), NOW()),
(32, 'PENSION', '양평',     30,   2, NOW(), NOW()),
(33, 'PENSION', '강원',     NULL, 2, NOW(), NOW()),
(34, 'PENSION', '강릉',     33,   1, NOW(), NOW()),
(35, 'PENSION', '평창',     33,   2, NOW(), NOW()),
(36, 'PENSION', '제주',     NULL, 3, NOW(), NOW());

-- POOL_VILLA 지역
INSERT INTO accommodation_region (id, accommodation_type, region_name, parent_id, sort_order, created_at, updated_at) VALUES
(37, 'POOL_VILLA', '제주',  NULL, 1, NOW(), NOW()),
(38, 'POOL_VILLA', '애월',  37,   1, NOW(), NOW()),
(39, 'POOL_VILLA', '성산',  37,   2, NOW(), NOW()),
(40, 'POOL_VILLA', '경기',  NULL, 2, NOW(), NOW()),
(41, 'POOL_VILLA', '가평',  40,   1, NOW(), NOW()),
(42, 'POOL_VILLA', '강원',  NULL, 3, NOW(), NOW());

-- GUEST_HOUSE 지역
INSERT INTO accommodation_region (id, accommodation_type, region_name, parent_id, sort_order, created_at, updated_at) VALUES
(43, 'GUEST_HOUSE', '서울',       NULL, 1, NOW(), NOW()),
(44, 'GUEST_HOUSE', '홍대',       43,   1, NOW(), NOW()),
(45, 'GUEST_HOUSE', '이태원',     43,   2, NOW(), NOW()),
(46, 'GUEST_HOUSE', '명동',       43,   3, NOW(), NOW()),
(47, 'GUEST_HOUSE', '부산',       NULL, 2, NOW(), NOW()),
(48, 'GUEST_HOUSE', '남포/중구',  47,   1, NOW(), NOW()),
(49, 'GUEST_HOUSE', '해운대',     47,   2, NOW(), NOW());

-- 기존 샘플 숙소에 지역 매핑
-- 1: 서울 그랜드 호텔 → HOTEL 강남구(2)
-- 2: 해운대 리조트 → RESORT 해운대(23)
-- 3: 홍대 게스트하우스 → GUEST_HOUSE 홍대(44)
-- 4: 역삼 모텔 → MOTEL 강남/역삼(14)
-- 5: 가평 숲속 펜션 → PENSION 가평(31)
-- 6: 제주 오션 풀빌라 → POOL_VILLA 애월(38)
UPDATE accommodation SET region_id = 2  WHERE id = 1;
UPDATE accommodation SET region_id = 23 WHERE id = 2;
UPDATE accommodation SET region_id = 44 WHERE id = 3;
UPDATE accommodation SET region_id = 14 WHERE id = 4;
UPDATE accommodation SET region_id = 31 WHERE id = 5;
UPDATE accommodation SET region_id = 38 WHERE id = 6;


-- ============================================
-- 다국어 번역 데이터 (en, ja)
-- ============================================

-- 숙소 번역 (accommodation_translation)
INSERT INTO accommodation_translation (accommodation_id, locale, name, full_address, location_description, created_at, updated_at)
VALUES
-- 서울 그랜드 호텔
(1, 'en', 'Seoul Grand Hotel',        '123 Teheran-ro, Gangnam-gu, Seoul',        '3-min walk from Exit 5, Gangnam Station', NOW(), NOW()),
(1, 'ja', 'ソウルグランドホテル',       'ソウル市江南区テヘラン路123',              '江南駅5番出口より徒歩3分',               NOW(), NOW()),
-- 해운대 리조트
(2, 'en', 'Haeundae Resort',           '456 Haeundae-ro, Haeundae-gu, Busan',      'Right in front of Haeundae Beach',        NOW(), NOW()),
(2, 'ja', '海雲台リゾート',             '釜山市海雲台区海雲台路456',               '海雲台海水浴場の目の前',                 NOW(), NOW()),
-- 홍대 게스트하우스
(3, 'en', 'Hongdae Guesthouse',        '789 Wausan-ro, Mapo-gu, Seoul',            'Exit 9, Hongik Univ. Station',            NOW(), NOW()),
(3, 'ja', '弘大ゲストハウス',           'ソウル市麻浦区臥牛山路789',               '弘大入口駅9番出口',                      NOW(), NOW()),
-- 역삼 모텔
(4, 'en', 'Yeoksam Motel',             '101 Yeoksam-dong, Gangnam-gu, Seoul',      'Exit 3, Yeoksam Station',                 NOW(), NOW()),
(4, 'ja', '駅三モーテル',               'ソウル市江南区駅三洞101',                 '驛三駅3番出口',                          NOW(), NOW()),
-- 가평 숲속 펜션
(5, 'en', 'Gapyeong Forest Pension',   '55 Baekdun-ro, Buknyeon, Gapyeong, Gyeonggi', '10 min by car from Jarasum Island',   NOW(), NOW()),
(5, 'ja', '加平森ペンション',            '京畿道加平郡北面白芚路55',               '자라섬まで車で10分',                    NOW(), NOW()),
-- 제주 오션 풀빌라
(6, 'en', 'Jeju Ocean Pool Villa',     '200 Haeandoro, Aewol-eup, Jeju',           'Located on Aewol Coastal Road',           NOW(), NOW()),
(6, 'ja', '済州オーシャンプールヴィラ', '済州市涯月邑海岸路200',                   '涯月海岸道路沿い',                       NOW(), NOW());

-- 객실 번역 (room_translation)
INSERT INTO room_translation (room_id, locale, name, room_type_name, created_at, updated_at)
VALUES
-- 디럭스 더블 (room 1)
(1, 'en', 'Deluxe Double',          'Deluxe',     NOW(), NOW()),
(1, 'ja', 'デラックスダブル',        'デラックス', NOW(), NOW()),
-- 스위트룸 (room 2)
(2, 'en', 'Suite Room',             'Suite',      NOW(), NOW()),
(2, 'ja', 'スイートルーム',          'スイート',   NOW(), NOW()),
-- 오션뷰 디럭스 (room 3)
(3, 'en', 'Ocean View Deluxe',      'Deluxe',     NOW(), NOW()),
(3, 'ja', 'オーシャンビューデラックス', 'デラックス', NOW(), NOW()),
-- 4인 도미토리 (room 4)
(4, 'en', '4-Person Dormitory',     'Dormitory',  NOW(), NOW()),
(4, 'ja', '4人ドミトリー',           'ドミトリー', NOW(), NOW()),
-- 201호 (room 5)
(5, 'en', 'Room 201',               'Standard',   NOW(), NOW()),
(5, 'ja', '201号室',                 'スタンダード', NOW(), NOW()),
-- 202호 (room 6)
(6, 'en', 'Room 202',               'Standard',   NOW(), NOW()),
(6, 'ja', '202号室',                 'スタンダード', NOW(), NOW()),
-- 숲속 A동 (room 7)
(7, 'en', 'Forest Building A',      'Family',     NOW(), NOW()),
(7, 'ja', '森の棟A',                 'ファミリー', NOW(), NOW()),
-- 오션뷰 빌라 (room 8)
(8, 'en', 'Ocean View Villa',       'Villa',      NOW(), NOW()),
(8, 'ja', 'オーシャンビューヴィラ',  'ヴィラ',     NOW(), NOW());

-- 객실 옵션 번역 (room_option_translation)
INSERT INTO room_option_translation (room_option_id, locale, name, created_at, updated_at)
VALUES
-- 기본 (option 1, room 1 디럭스 더블)
(1,  'en', 'Standard',                  NOW(), NOW()),
(1,  'ja', 'スタンダード',               NOW(), NOW()),
-- 조식 포함 (option 2)
(2,  'en', 'Breakfast Included',        NOW(), NOW()),
(2,  'ja', '朝食付き',                   NOW(), NOW()),
-- 기본 (option 3, room 2 스위트)
(3,  'en', 'Standard',                  NOW(), NOW()),
(3,  'ja', 'スタンダード',               NOW(), NOW()),
-- 조식+스파 패키지 (option 4)
(4,  'en', 'Breakfast & Spa Package',   NOW(), NOW()),
(4,  'ja', '朝食＋スパパッケージ',       NOW(), NOW()),
-- 기본 (option 5, 리조트)
(5,  'en', 'Standard',                  NOW(), NOW()),
(5,  'ja', 'スタンダード',               NOW(), NOW()),
-- 기본 (option 6, 게스트하우스)
(6,  'en', 'Standard',                  NOW(), NOW()),
(6,  'ja', 'スタンダード',               NOW(), NOW()),
-- 기본 (option 7, 모텔 201호)
(7,  'en', 'Standard',                  NOW(), NOW()),
(7,  'ja', 'スタンダード',               NOW(), NOW()),
-- 기본 (option 8, 모텔 202호)
(8,  'en', 'Standard',                  NOW(), NOW()),
(8,  'ja', 'スタンダード',               NOW(), NOW()),
-- 기본 (option 9, 펜션)
(9,  'en', 'Standard',                  NOW(), NOW()),
(9,  'ja', 'スタンダード',               NOW(), NOW()),
-- 기본 (option 10, 풀빌라)
(10, 'en', 'Standard',                  NOW(), NOW()),
(10, 'ja', 'スタンダード',               NOW(), NOW());

-- ============================================
-- 추가 샘플 데이터 (지역별 검색 테스트용)
-- ============================================

-- 추가 숙소 (id 7-25)
INSERT INTO accommodation (id, partner_id, name, type, full_address, latitude, longitude, location_description, status, check_in_time, check_out_time, supplier_managed, created_at, updated_at)
VALUES
(7,  1, '서울 마포 호텔',             'HOTEL',      '서울시 마포구 독막로 30',             37.5490, 126.9160, '합정역 2번 출구 도보 5분',    'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(8,  1, '부산 해운대 호텔',           'HOTEL',      '부산시 해운대구 해운대해변로 30',     35.1588, 129.1603, '해운대 해변 인접',            'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(9,  2, '제주 시티 호텔',             'HOTEL',      '제주시 연동 1234',                    33.4996, 126.5312, '제주시내 중심부',             'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(10, 2, '서울 종로 호텔',             'HOTEL',      '서울시 종로구 인사동길 5',            37.5742, 126.9842, '안국역 1번 출구 도보 3분',    'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(11, 2, '잠실 스타 모텔',             'MOTEL',      '서울시 송파구 잠실로 100',            37.5133, 127.0975, '잠실역 3번 출구',             'ACTIVE', '21:00', '11:00', false, NOW(), NOW()),
(12, 3, '홍대 24 모텔',               'MOTEL',      '서울시 마포구 어울마당로 50',         37.5571, 126.9250, '홍대입구역 근처',             'ACTIVE', '21:00', '11:00', false, NOW(), NOW()),
(13, 3, '서면 베스트 모텔',           'MOTEL',      '부산시 부산진구 서면로 10',           35.1570, 129.0590, '서면역 1번 출구',             'ACTIVE', '21:00', '11:00', false, NOW(), NOW()),
(14, 1, '해운대 뷰 모텔',             'MOTEL',      '부산시 해운대구 구남로 20',           35.1600, 129.1620, '해운대역 5번 출구',           'ACTIVE', '21:00', '11:00', false, NOW(), NOW()),
(15, 1, '서귀포 클리프 리조트',       'RESORT',     '서귀포시 중문관광로 72',              33.2517, 126.4130, '중문관광단지 내',             'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(16, 2, '속초 오션 리조트',           'RESORT',     '강원도 속초시 해오름로 100',          38.2070, 128.5920, '속초 해변 인접',              'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(17, 2, '강릉 씨크루즈 리조트',       'RESORT',     '강원도 강릉시 헌화로 950',            37.7820, 128.9570, '정동진 해안 뷰',              'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(18, 3, '양평 강변 펜션',             'PENSION',    '경기도 양평군 양서면 목왕로 10',      37.5150, 127.4950, '두물머리 차량 10분',          'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(19, 3, '강릉 솔향 펜션',             'PENSION',    '강원도 강릉시 사천면 해안로 300',     37.9200, 128.8780, '사천 해변 도보 2분',          'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(20, 1, '평창 별빛 펜션',             'PENSION',    '강원도 평창군 진부면 오대산로 50',    37.6340, 128.6210, '오대산 국립공원 입구',        'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(21, 1, '성산 선라이즈 풀빌라',       'POOL_VILLA', '제주시 성산읍 일출로 200',            33.4590, 126.9310, '성산일출봉 차량 5분',         'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(22, 2, '가평 포레스트 풀빌라',       'POOL_VILLA', '경기도 가평군 청평면 호반로 50',      37.7350, 127.4850, '청평 호수 인접',              'ACTIVE', '15:00', '11:00', false, NOW(), NOW()),
(23, 3, '이태원 글로벌 게스트하우스', 'GUEST_HOUSE','서울시 용산구 이태원로 150',          37.5350, 126.9950, '이태원역 3번 출구',           'ACTIVE', '16:00', '10:00', false, NOW(), NOW()),
(24, 3, '명동 스테이 게스트하우스',   'GUEST_HOUSE','서울시 중구 명동길 30',              37.5636, 126.9827, '명동역 5번 출구',             'ACTIVE', '16:00', '10:00', false, NOW(), NOW()),
(25, 1, '해운대 비치 게스트하우스',   'GUEST_HOUSE','부산시 해운대구 구남로 100',          35.1590, 129.1605, '해운대 해변 도보 3분',        'ACTIVE', '16:00', '10:00', false, NOW(), NOW());

-- 추가 숙소 이미지
INSERT INTO accommodation_image (id, accommodation_id, relative_path, category, display_order, is_primary) VALUES
(8,  7,  '/accommodation/hotel/mapo_main.png',          'EXTERIOR', 1, true),
(9,  8,  '/accommodation/hotel/haeundae_main.png',      'EXTERIOR', 1, true),
(10, 9,  '/accommodation/hotel/jeju_main.png',          'EXTERIOR', 1, true),
(11, 10, '/accommodation/hotel/jongro_main.png',        'EXTERIOR', 1, true),
(12, 11, '/accommodation/motel/jamsil_main.png',        'EXTERIOR', 1, true),
(13, 12, '/accommodation/motel/hongdae_main.png',       'EXTERIOR', 1, true),
(14, 13, '/accommodation/motel/seomyeon_main.png',      'EXTERIOR', 1, true),
(15, 14, '/accommodation/motel/haeundae_main.png',      'EXTERIOR', 1, true),
(16, 15, '/accommodation/resort/seogwipo_main.png',     'EXTERIOR', 1, true),
(17, 16, '/accommodation/resort/sokcho_main.png',       'EXTERIOR', 1, true),
(18, 17, '/accommodation/resort/gangneung_main.png',    'EXTERIOR', 1, true),
(19, 18, '/accommodation/pension/yangpyeong_main.png',  'EXTERIOR', 1, true),
(20, 19, '/accommodation/pension/gangneung_main.png',   'EXTERIOR', 1, true),
(21, 20, '/accommodation/pension/pyeongchang_main.png', 'EXTERIOR', 1, true),
(22, 21, '/accommodation/poolvilla/seongsan_main.png',  'EXTERIOR', 1, true),
(23, 22, '/accommodation/poolvilla/gapyeong_main.png',  'EXTERIOR', 1, true),
(24, 23, '/accommodation/guesthouse/itaewon_main.png',  'EXTERIOR', 1, true),
(25, 24, '/accommodation/guesthouse/myeongdong_main.png','EXTERIOR',1, true),
(26, 25, '/accommodation/guesthouse/haeundae_main.png', 'EXTERIOR', 1, true);

-- 추가 객실 (room 9-27)
INSERT INTO room (id, accommodation_id, name, room_type_name, standard_capacity, max_capacity, status, created_at, updated_at) VALUES
(9,  7,  '스탠다드 더블',   '스탠다드', 2, 3, 'ACTIVE', NOW(), NOW()),
(10, 8,  '시뷰 디럭스',     '디럭스',   2, 4, 'ACTIVE', NOW(), NOW()),
(11, 9,  '시티뷰 더블',     '스탠다드', 2, 3, 'ACTIVE', NOW(), NOW()),
(12, 10, '한옥 스타일룸',   '스탠다드', 2, 3, 'ACTIVE', NOW(), NOW()),
(13, 11, '일반실',          '스탠다드', 2, 2, 'ACTIVE', NOW(), NOW()),
(14, 12, '더블룸',          '스탠다드', 2, 2, 'ACTIVE', NOW(), NOW()),
(15, 13, '스탠다드룸',      '스탠다드', 2, 2, 'ACTIVE', NOW(), NOW()),
(16, 14, '오션뷰룸',        '스탠다드', 2, 2, 'ACTIVE', NOW(), NOW()),
(17, 15, '클리프뷰 디럭스', '디럭스',   2, 4, 'ACTIVE', NOW(), NOW()),
(18, 16, '오션뷰 스위트',   '스위트',   2, 4, 'ACTIVE', NOW(), NOW()),
(19, 17, '씨뷰 디럭스',     '디럭스',   2, 4, 'ACTIVE', NOW(), NOW()),
(20, 18, '리버뷰 객실',     '패밀리',   4, 6, 'ACTIVE', NOW(), NOW()),
(21, 19, '솔향 객실',       '패밀리',   4, 6, 'ACTIVE', NOW(), NOW()),
(22, 20, '산장 객실',       '패밀리',   4, 6, 'ACTIVE', NOW(), NOW()),
(23, 21, '선라이즈 빌라',   '빌라',     4, 8, 'ACTIVE', NOW(), NOW()),
(24, 22, '포레스트 빌라',   '빌라',     4, 8, 'ACTIVE', NOW(), NOW()),
(25, 23, '4인 도미토리',    '도미토리', 1, 4, 'ACTIVE', NOW(), NOW()),
(26, 24, '4인 도미토리',    '도미토리', 1, 4, 'ACTIVE', NOW(), NOW()),
(27, 25, '비치뷰 도미토리', '도미토리', 1, 4, 'ACTIVE', NOW(), NOW());

-- 추가 객실 옵션 (option 11-29)
INSERT INTO room_option (id, room_id, name, cancellation_policy, additional_price, created_at, updated_at) VALUES
(11, 9,  '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(12, 10, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(13, 11, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(14, 12, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(15, 13, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(16, 14, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(17, 15, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(18, 16, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(19, 17, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(20, 18, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(21, 19, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(22, 20, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(23, 21, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(24, 22, '기본', 'PARTIAL_REFUND',    0, NOW(), NOW()),
(25, 23, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(26, 24, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(27, 25, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(28, 26, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW()),
(29, 27, '기본', 'FREE_CANCELLATION', 0, NOW(), NOW());

-- 추가 재고 (options 11-29, 2026-04-22 ~ 2026-05-10)
-- HOTEL options (11-14): 5실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(11,'2026-04-22',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-23',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-24',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-25',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-26',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-27',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-28',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-29',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-04-30',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-01',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-02',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-03',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-04',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-05',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-06',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-07',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-08',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-09',5,5,'AVAILABLE',NOW(),NOW()),(11,'2026-05-10',5,5,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(12,'2026-04-22',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-23',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-24',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-25',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-26',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-27',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-28',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-29',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-04-30',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-01',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-02',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-03',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-04',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-05',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-06',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-07',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-08',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-09',5,5,'AVAILABLE',NOW(),NOW()),(12,'2026-05-10',5,5,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(13,'2026-04-22',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-23',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-24',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-25',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-26',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-27',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-28',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-29',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-04-30',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-01',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-02',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-03',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-04',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-05',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-06',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-07',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-08',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-09',5,5,'AVAILABLE',NOW(),NOW()),(13,'2026-05-10',5,5,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(14,'2026-04-22',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-23',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-24',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-25',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-26',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-27',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-28',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-29',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-04-30',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-01',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-02',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-03',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-04',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-05',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-06',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-07',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-08',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-09',5,5,'AVAILABLE',NOW(),NOW()),(14,'2026-05-10',5,5,'AVAILABLE',NOW(),NOW());
-- MOTEL options (15-18): 2실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(15,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-06',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-07',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-08',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-09',2,2,'AVAILABLE',NOW(),NOW()),(15,'2026-05-10',2,2,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(16,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-06',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-07',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-08',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-09',2,2,'AVAILABLE',NOW(),NOW()),(16,'2026-05-10',2,2,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(17,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-06',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-07',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-08',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-09',2,2,'AVAILABLE',NOW(),NOW()),(17,'2026-05-10',2,2,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(18,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-06',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-07',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-08',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-09',2,2,'AVAILABLE',NOW(),NOW()),(18,'2026-05-10',2,2,'AVAILABLE',NOW(),NOW());
-- RESORT options (19-21): 4실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(19,'2026-04-22',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-23',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-24',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-25',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-26',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-27',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-28',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-29',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-04-30',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-01',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-02',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-03',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-04',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-05',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-06',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-07',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-08',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-09',4,4,'AVAILABLE',NOW(),NOW()),(19,'2026-05-10',4,4,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(20,'2026-04-22',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-23',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-24',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-25',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-26',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-27',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-28',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-29',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-04-30',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-01',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-02',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-03',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-04',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-05',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-06',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-07',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-08',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-09',4,4,'AVAILABLE',NOW(),NOW()),(20,'2026-05-10',4,4,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(21,'2026-04-22',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-23',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-24',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-25',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-26',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-27',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-28',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-29',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-04-30',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-01',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-02',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-03',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-04',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-05',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-06',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-07',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-08',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-09',4,4,'AVAILABLE',NOW(),NOW()),(21,'2026-05-10',4,4,'AVAILABLE',NOW(),NOW());
-- PENSION options (22-24): 3실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(22,'2026-04-22',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-23',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-24',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-28',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-29',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-04-30',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-01',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-02',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-03',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-04',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-05',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-06',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-07',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-08',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-09',3,3,'AVAILABLE',NOW(),NOW()),(22,'2026-05-10',3,3,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(23,'2026-04-22',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-23',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-24',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-28',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-29',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-04-30',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-01',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-02',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-03',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-04',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-05',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-06',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-07',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-08',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-09',3,3,'AVAILABLE',NOW(),NOW()),(23,'2026-05-10',3,3,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(24,'2026-04-22',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-23',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-24',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-25',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-26',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-27',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-28',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-29',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-04-30',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-01',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-02',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-03',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-04',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-05',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-06',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-07',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-08',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-09',3,3,'AVAILABLE',NOW(),NOW()),(24,'2026-05-10',3,3,'AVAILABLE',NOW(),NOW());
-- POOL_VILLA options (25-26): 2실
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(25,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-06',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-07',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-08',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-09',2,2,'AVAILABLE',NOW(),NOW()),(25,'2026-05-10',2,2,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(26,'2026-04-22',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-23',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-24',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-25',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-26',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-27',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-28',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-29',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-04-30',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-01',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-02',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-03',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-04',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-05',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-06',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-07',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-08',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-09',2,2,'AVAILABLE',NOW(),NOW()),(26,'2026-05-10',2,2,'AVAILABLE',NOW(),NOW());
-- GUEST_HOUSE options (27-29): 6베드
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(27,'2026-04-22',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-23',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-24',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-25',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-26',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-27',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-28',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-29',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-04-30',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-01',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-02',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-03',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-04',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-05',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-06',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-07',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-08',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-09',6,6,'AVAILABLE',NOW(),NOW()),(27,'2026-05-10',6,6,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(28,'2026-04-22',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-23',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-24',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-25',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-26',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-27',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-28',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-29',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-04-30',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-01',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-02',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-03',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-04',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-05',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-06',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-07',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-08',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-09',6,6,'AVAILABLE',NOW(),NOW()),(28,'2026-05-10',6,6,'AVAILABLE',NOW(),NOW());
INSERT INTO inventory (room_option_id, date, total_quantity, remaining_quantity, status, created_at, updated_at) VALUES
(29,'2026-04-22',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-23',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-24',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-25',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-26',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-27',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-28',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-29',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-04-30',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-01',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-02',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-03',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-04',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-05',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-06',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-07',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-08',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-09',6,6,'AVAILABLE',NOW(),NOW()),(29,'2026-05-10',6,6,'AVAILABLE',NOW(),NOW());

-- 추가 요금 (options 11-29)
-- HOTEL (11-14): 130,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(11,'2026-04-22','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-04-23','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-04-24','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-04-25','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-04-26','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-04-27','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-04-28','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-04-29','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-04-30','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-05-01','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-05-02','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-05-03','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-05-04','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-05-05','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-05-06','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-05-07','STAY',160000,130000,true,NOW(),NOW()),(11,'2026-05-08','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-05-09','STAY',190000,160000,true,NOW(),NOW()),(11,'2026-05-10','STAY',190000,160000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(12,'2026-04-22','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-04-23','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-04-24','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-04-25','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-04-26','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-04-27','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-04-28','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-04-29','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-04-30','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-05-01','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-05-02','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-05-03','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-05-04','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-05-05','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-05-06','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-05-07','STAY',180000,150000,true,NOW(),NOW()),(12,'2026-05-08','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-05-09','STAY',210000,180000,true,NOW(),NOW()),(12,'2026-05-10','STAY',210000,180000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(13,'2026-04-22','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-04-23','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-04-24','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-04-25','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-04-26','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-04-27','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-04-28','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-04-29','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-04-30','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-05-01','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-05-02','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-05-03','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-05-04','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-05-05','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-05-06','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-05-07','STAY',150000,120000,true,NOW(),NOW()),(13,'2026-05-08','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-05-09','STAY',180000,150000,true,NOW(),NOW()),(13,'2026-05-10','STAY',180000,150000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(14,'2026-04-22','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-04-23','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-04-24','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-04-25','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-04-26','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-04-27','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-04-28','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-04-29','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-04-30','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-05-01','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-05-02','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-05-03','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-05-04','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-05-05','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-05-06','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-05-07','STAY',160000,130000,true,NOW(),NOW()),(14,'2026-05-08','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-05-09','STAY',190000,160000,true,NOW(),NOW()),(14,'2026-05-10','STAY',190000,160000,true,NOW(),NOW());
-- MOTEL (15-18): 60,000 STAY + 30,000 HOURLY
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(15,'2026-04-22','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-04-23','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-04-24','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-04-25','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-04-26','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-04-27','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-04-28','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-04-29','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-04-30','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-05-01','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-05-02','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-05-03','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-05-04','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-05-05','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-05-06','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-05-07','STAY',75000,60000,true,NOW(),NOW()),(15,'2026-05-08','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-05-09','STAY',85000,70000,true,NOW(),NOW()),(15,'2026-05-10','STAY',85000,70000,true,NOW(),NOW()),
(15,'2026-04-22','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-23','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-24','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-26','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-27','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-28','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-29','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-04-30','HOURLY',40000,30000,true,NOW(),NOW()),(15,'2026-05-01','HOURLY',40000,30000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(16,'2026-04-22','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-04-23','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-04-24','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-04-25','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-04-26','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-04-27','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-04-28','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-04-29','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-04-30','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-05-01','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-05-02','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-05-03','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-05-04','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-05-05','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-05-06','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-05-07','STAY',75000,60000,true,NOW(),NOW()),(16,'2026-05-08','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-05-09','STAY',85000,70000,true,NOW(),NOW()),(16,'2026-05-10','STAY',85000,70000,true,NOW(),NOW()),
(16,'2026-04-22','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-23','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-24','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-26','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-27','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-28','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-29','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-04-30','HOURLY',40000,30000,true,NOW(),NOW()),(16,'2026-05-01','HOURLY',40000,30000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(17,'2026-04-22','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-04-23','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-04-24','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-04-25','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-04-26','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-04-27','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-04-28','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-04-29','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-04-30','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-05-01','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-05-02','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-05-03','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-05-04','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-05-05','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-05-06','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-05-07','STAY',70000,55000,true,NOW(),NOW()),(17,'2026-05-08','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-05-09','STAY',80000,65000,true,NOW(),NOW()),(17,'2026-05-10','STAY',80000,65000,true,NOW(),NOW()),
(17,'2026-04-22','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-23','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-24','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-25','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-26','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-27','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-28','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-29','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-04-30','HOURLY',35000,28000,true,NOW(),NOW()),(17,'2026-05-01','HOURLY',35000,28000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(18,'2026-04-22','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-04-23','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-04-24','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-04-25','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-04-26','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-04-27','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-04-28','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-04-29','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-04-30','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-05-01','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-05-02','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-05-03','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-05-04','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-05-05','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-05-06','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-05-07','STAY',75000,60000,true,NOW(),NOW()),(18,'2026-05-08','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-05-09','STAY',85000,70000,true,NOW(),NOW()),(18,'2026-05-10','STAY',85000,70000,true,NOW(),NOW()),
(18,'2026-04-22','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-23','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-24','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-25','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-26','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-27','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-28','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-29','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-04-30','HOURLY',40000,30000,true,NOW(),NOW()),(18,'2026-05-01','HOURLY',40000,30000,true,NOW(),NOW());
-- RESORT (19-21): 170,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(19,'2026-04-22','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-04-23','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-04-24','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-04-25','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-04-26','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-04-27','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-04-28','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-04-29','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-04-30','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-05-01','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-05-02','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-05-03','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-05-04','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-05-05','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-05-06','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-05-07','STAY',200000,170000,true,NOW(),NOW()),(19,'2026-05-08','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-05-09','STAY',240000,210000,true,NOW(),NOW()),(19,'2026-05-10','STAY',240000,210000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(20,'2026-04-22','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-04-23','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-04-24','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-04-25','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-04-26','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-04-27','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-04-28','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-04-29','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-04-30','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-05-01','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-05-02','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-05-03','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-05-04','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-05-05','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-05-06','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-05-07','STAY',190000,160000,true,NOW(),NOW()),(20,'2026-05-08','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-05-09','STAY',230000,200000,true,NOW(),NOW()),(20,'2026-05-10','STAY',230000,200000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(21,'2026-04-22','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-04-23','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-04-24','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-04-25','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-04-26','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-04-27','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-04-28','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-04-29','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-04-30','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-05-01','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-05-02','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-05-03','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-05-04','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-05-05','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-05-06','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-05-07','STAY',200000,170000,true,NOW(),NOW()),(21,'2026-05-08','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-05-09','STAY',240000,210000,true,NOW(),NOW()),(21,'2026-05-10','STAY',240000,210000,true,NOW(),NOW());
-- PENSION (22-24): 130,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(22,'2026-04-22','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-04-23','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-04-24','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-04-25','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-04-26','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-04-27','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-04-28','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-04-29','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-04-30','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-05-01','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-05-02','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-05-03','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-05-04','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-05-05','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-05-06','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-05-07','STAY',160000,130000,true,NOW(),NOW()),(22,'2026-05-08','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-05-09','STAY',200000,170000,true,NOW(),NOW()),(22,'2026-05-10','STAY',200000,170000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(23,'2026-04-22','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-04-23','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-04-24','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-04-25','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-04-26','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-04-27','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-04-28','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-04-29','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-04-30','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-05-01','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-05-02','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-05-03','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-05-04','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-05-05','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-05-06','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-05-07','STAY',160000,130000,true,NOW(),NOW()),(23,'2026-05-08','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-05-09','STAY',200000,170000,true,NOW(),NOW()),(23,'2026-05-10','STAY',200000,170000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(24,'2026-04-22','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-04-23','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-04-24','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-04-25','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-04-26','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-04-27','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-04-28','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-04-29','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-04-30','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-05-01','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-05-02','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-05-03','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-05-04','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-05-05','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-05-06','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-05-07','STAY',150000,120000,true,NOW(),NOW()),(24,'2026-05-08','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-05-09','STAY',190000,160000,true,NOW(),NOW()),(24,'2026-05-10','STAY',190000,160000,true,NOW(),NOW());
-- POOL_VILLA (25-26): 300,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(25,'2026-04-22','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-04-23','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-04-24','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-04-25','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-04-26','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-04-27','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-04-28','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-04-29','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-04-30','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-05-01','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-05-02','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-05-03','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-05-04','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-05-05','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-05-06','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-05-07','STAY',380000,300000,true,NOW(),NOW()),(25,'2026-05-08','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-05-09','STAY',480000,400000,true,NOW(),NOW()),(25,'2026-05-10','STAY',480000,400000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(26,'2026-04-22','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-04-23','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-04-24','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-04-25','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-04-26','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-04-27','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-04-28','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-04-29','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-04-30','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-05-01','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-05-02','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-05-03','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-05-04','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-05-05','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-05-06','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-05-07','STAY',360000,280000,true,NOW(),NOW()),(26,'2026-05-08','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-05-09','STAY',440000,380000,true,NOW(),NOW()),(26,'2026-05-10','STAY',440000,380000,true,NOW(),NOW());
-- GUEST_HOUSE (27-29): 28,000
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(27,'2026-04-22','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-04-23','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-04-24','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-04-25','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-04-26','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-04-27','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-04-28','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-04-29','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-04-30','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-05-01','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-05-02','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-05-03','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-05-04','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-05-05','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-05-06','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-05-07','STAY',35000,28000,true,NOW(),NOW()),(27,'2026-05-08','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-05-09','STAY',40000,33000,true,NOW(),NOW()),(27,'2026-05-10','STAY',40000,33000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(28,'2026-04-22','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-04-23','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-04-24','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-04-25','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-04-26','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-04-27','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-04-28','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-04-29','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-04-30','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-05-01','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-05-02','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-05-03','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-05-04','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-05-05','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-05-06','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-05-07','STAY',35000,28000,true,NOW(),NOW()),(28,'2026-05-08','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-05-09','STAY',40000,33000,true,NOW(),NOW()),(28,'2026-05-10','STAY',40000,33000,true,NOW(),NOW());
INSERT INTO room_price (room_option_id, date, price_type, base_price, selling_price, tax_included, created_at, updated_at) VALUES
(29,'2026-04-22','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-04-23','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-04-24','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-04-25','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-04-26','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-04-27','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-04-28','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-04-29','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-04-30','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-05-01','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-05-02','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-05-03','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-05-04','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-05-05','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-05-06','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-05-07','STAY',35000,28000,true,NOW(),NOW()),(29,'2026-05-08','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-05-09','STAY',40000,33000,true,NOW(),NOW()),(29,'2026-05-10','STAY',40000,33000,true,NOW(),NOW());

-- 추가 숙소 지역 매핑
UPDATE accommodation SET region_id = 3  WHERE id = 7;   -- 마포구
UPDATE accommodation SET region_id = 7  WHERE id = 8;   -- 해운대구
UPDATE accommodation SET region_id = 10 WHERE id = 9;   -- 제주시
UPDATE accommodation SET region_id = 5  WHERE id = 10;  -- 종로구
UPDATE accommodation SET region_id = 13 WHERE id = 11;  -- 잠실/송파
UPDATE accommodation SET region_id = 15 WHERE id = 12;  -- 홍대/마포
UPDATE accommodation SET region_id = 19 WHERE id = 13;  -- 서면
UPDATE accommodation SET region_id = 20 WHERE id = 14;  -- 해운대(MOTEL)
UPDATE accommodation SET region_id = 26 WHERE id = 15;  -- 서귀포
UPDATE accommodation SET region_id = 28 WHERE id = 16;  -- 속초
UPDATE accommodation SET region_id = 29 WHERE id = 17;  -- 강릉(RESORT)
UPDATE accommodation SET region_id = 32 WHERE id = 18;  -- 양평
UPDATE accommodation SET region_id = 34 WHERE id = 19;  -- 강릉(PENSION)
UPDATE accommodation SET region_id = 35 WHERE id = 20;  -- 평창
UPDATE accommodation SET region_id = 39 WHERE id = 21;  -- 성산
UPDATE accommodation SET region_id = 41 WHERE id = 22;  -- 가평(POOL_VILLA)
UPDATE accommodation SET region_id = 45 WHERE id = 23;  -- 이태원
UPDATE accommodation SET region_id = 46 WHERE id = 24;  -- 명동
UPDATE accommodation SET region_id = 49 WHERE id = 25;  -- 해운대(GUEST_HOUSE)

-- 추가 숙소 태그 매핑
INSERT INTO accommodation_tag (accommodation_id, tag_id, created_at, updated_at) VALUES
-- HOTEL (7-10): 수영장,피트니스,주차장,룸서비스,컨시어지
(7, 1, NOW(), NOW()),(7, 3, NOW(), NOW()),(7, 4, NOW(), NOW()),(7, 5, NOW(), NOW()),(7, 7, NOW(), NOW()),
(8, 1, NOW(), NOW()),(8, 2, NOW(), NOW()),(8, 3, NOW(), NOW()),(8, 4, NOW(), NOW()),(8, 5, NOW(), NOW()),(8, 6, NOW(), NOW()),(8, 7, NOW(), NOW()),
(9, 3, NOW(), NOW()),(9, 4, NOW(), NOW()),(9, 5, NOW(), NOW()),(9, 7, NOW(), NOW()),
(10, 4, NOW(), NOW()),(10, 5, NOW(), NOW()),(10, 7, NOW(), NOW()),(10, 8, NOW(), NOW()),
-- MOTEL (11-14): 주차무료,OTT,무인체크인
(11, 4, NOW(), NOW()),(11, 12, NOW(), NOW()),(11, 13, NOW(), NOW()),(11, 14, NOW(), NOW()),
(12, 4, NOW(), NOW()),(12, 12, NOW(), NOW()),(12, 13, NOW(), NOW()),(12, 14, NOW(), NOW()),
(13, 4, NOW(), NOW()),(13, 12, NOW(), NOW()),(13, 13, NOW(), NOW()),(13, 14, NOW(), NOW()),
(14, 4, NOW(), NOW()),(14, 12, NOW(), NOW()),(14, 13, NOW(), NOW()),(14, 14, NOW(), NOW()),
-- RESORT (15-17): 수영장,피트니스,주차장,워터파크
(15, 1, NOW(), NOW()),(15, 3, NOW(), NOW()),(15, 4, NOW(), NOW()),(15, 9, NOW(), NOW()),(15, 10, NOW(), NOW()),
(16, 1, NOW(), NOW()),(16, 3, NOW(), NOW()),(16, 4, NOW(), NOW()),(16, 9, NOW(), NOW()),
(17, 1, NOW(), NOW()),(17, 3, NOW(), NOW()),(17, 4, NOW(), NOW()),(17, 11, NOW(), NOW()),
-- PENSION (18-20): 주차장,BBQ,반려동물
(18, 4, NOW(), NOW()),(18, 15, NOW(), NOW()),(18, 16, NOW(), NOW()),(18, 17, NOW(), NOW()),
(19, 4, NOW(), NOW()),(19, 15, NOW(), NOW()),(19, 16, NOW(), NOW()),(19, 17, NOW(), NOW()),(19, 18, NOW(), NOW()),
(20, 4, NOW(), NOW()),(20, 15, NOW(), NOW()),(20, 18, NOW(), NOW()),
-- POOL_VILLA (21-22): 수영장,주차장,개별수영장,자쿠지
(21, 1, NOW(), NOW()),(21, 4, NOW(), NOW()),(21, 19, NOW(), NOW()),(21, 20, NOW(), NOW()),(21, 21, NOW(), NOW()),
(22, 1, NOW(), NOW()),(22, 4, NOW(), NOW()),(22, 19, NOW(), NOW()),(22, 21, NOW(), NOW()),
-- GUEST_HOUSE (23-25): 주차장,공용주방,라운지,투어프로그램
(23, 4, NOW(), NOW()),(23, 22, NOW(), NOW()),(23, 23, NOW(), NOW()),(23, 24, NOW(), NOW()),
(24, 4, NOW(), NOW()),(24, 22, NOW(), NOW()),(24, 23, NOW(), NOW()),(24, 24, NOW(), NOW()),
(25, 4, NOW(), NOW()),(25, 22, NOW(), NOW()),(25, 23, NOW(), NOW()),(25, 24, NOW(), NOW());

-- 추가 숙소 번역 (accommodation_translation)
INSERT INTO accommodation_translation (accommodation_id, locale, name, full_address, location_description, created_at, updated_at) VALUES
(7,  'en', 'Seoul Mapo Hotel',             '30 Dongmak-ro, Mapo-gu, Seoul',               '5-min walk from Exit 2, Hapjeong Station',   NOW(), NOW()),
(7,  'ja', 'ソウル麻浦ホテル',              'ソウル市麻浦区東幕路30',                       '合井駅2番出口より徒歩5分',                   NOW(), NOW()),
(8,  'en', 'Busan Haeundae Hotel',          '30 Haeundae Beach Rd, Haeundae-gu, Busan',    'Adjacent to Haeundae Beach',                 NOW(), NOW()),
(8,  'ja', '釜山海雲台ホテル',              '釜山市海雲台区海雲台海辺路30',                '海雲台ビーチ隣接',                           NOW(), NOW()),
(9,  'en', 'Jeju City Hotel',               '1234 Yeondong, Jeju-si',                      'Downtown Jeju City Center',                  NOW(), NOW()),
(9,  'ja', '済州シティホテル',              '済州市連洞1234',                               '済州市内中心部',                             NOW(), NOW()),
(10, 'en', 'Seoul Jongno Hotel',            '5 Insadong-gil, Jongno-gu, Seoul',            '3-min walk from Exit 1, Anguk Station',      NOW(), NOW()),
(10, 'ja', 'ソウル鍾路ホテル',              'ソウル市鍾路区仁寺洞キル5',                   '安国駅1番出口より徒歩3分',                   NOW(), NOW()),
(11, 'en', 'Jamsil Star Motel',             '100 Jamsil-ro, Songpa-gu, Seoul',             'Exit 3, Jamsil Station',                     NOW(), NOW()),
(11, 'ja', '蚕室スターモーテル',            'ソウル市松坡区蚕室路100',                     '蚕室駅3番出口',                              NOW(), NOW()),
(12, 'en', 'Hongdae 24 Motel',              '50 Eoulmadang-ro, Mapo-gu, Seoul',            'Near Hongik Univ. Station',                  NOW(), NOW()),
(12, 'ja', '弘大24モーテル',               'ソウル市麻浦区ウルマダン路50',                '弘大入口駅近く',                             NOW(), NOW()),
(13, 'en', 'Seomyeon Best Motel',           '10 Seomyeon-ro, Busanjin-gu, Busan',          'Exit 1, Seomyeon Station',                   NOW(), NOW()),
(13, 'ja', '西面ベストモーテル',            '釜山市釜山鎮区西面路10',                      '西面駅1番出口',                              NOW(), NOW()),
(14, 'en', 'Haeundae View Motel',           '20 Gunam-ro, Haeundae-gu, Busan',             'Exit 5, Haeundae Station',                   NOW(), NOW()),
(14, 'ja', '海雲台ビューモーテル',          '釜山市海雲台区亀南路20',                      '海雲台駅5番出口',                            NOW(), NOW()),
(15, 'en', 'Seogwipo Cliff Resort',         '72 Jungmun Gwangwang-ro, Seogwipo',           'Inside Jungmun Tourist Complex',             NOW(), NOW()),
(15, 'ja', '西帰浦クリフリゾート',          '西帰浦市中文観光路72',                        '中文観光団地内',                             NOW(), NOW()),
(16, 'en', 'Sokcho Ocean Resort',           '100 Haeoruem-ro, Sokcho, Gangwon',            'Adjacent to Sokcho Beach',                   NOW(), NOW()),
(16, 'ja', '束草オーシャンリゾート',        '江原道束草市海オルム路100',                   '束草ビーチ隣接',                             NOW(), NOW()),
(17, 'en', 'Gangneung SeaCruise Resort',    '950 Heonhwa-ro, Gangneung, Gangwon',          'Jeongdongjin Coastal View',                  NOW(), NOW()),
(17, 'ja', '江陵シークルーズリゾート',      '江原道江陵市軒花路950',                       '正東津海岸ビュー',                           NOW(), NOW()),
(18, 'en', 'Yangpyeong Riverside Pension',  '10 Mokwang-ro, Yangyang-myeon, Yangpyeong',   '10 min by car from Dumulmeori',              NOW(), NOW()),
(18, 'ja', '楊平リバーサイドペンション',   '京畿道楊平郡楊西面木旺路10',                  '두물머리まで車で10分',                       NOW(), NOW()),
(19, 'en', 'Gangneung Pine Pension',        '300 Haean-ro, Sacheon-myeon, Gangneung',      '2-min walk to Sacheon Beach',                NOW(), NOW()),
(19, 'ja', '江陵松香ペンション',            '江原道江陵市沙川面海岸路300',                 '沙川ビーチ徒歩2分',                          NOW(), NOW()),
(20, 'en', 'Pyeongchang Starlight Pension', '50 Odaesan-ro, Jinbu-myeon, Pyeongchang',     'Odaesan National Park Entrance',             NOW(), NOW()),
(20, 'ja', '平昌スターライトペンション',    '江原道平昌郡珍富面五台山路50',               '五台山国立公園入口',                         NOW(), NOW()),
(21, 'en', 'Seongsan Sunrise Pool Villa',   '200 Ilchul-ro, Seongsan-eup, Jeju',           '5 min by car from Seongsan Ilchulbong',      NOW(), NOW()),
(21, 'ja', '城山サンライズプールヴィラ',    '済州市城山邑日出路200',                       '城山日出峰まで車で5分',                      NOW(), NOW()),
(22, 'en', 'Gapyeong Forest Pool Villa',    '50 Hoban-ro, Cheongpyeong-myeon, Gapyeong',  'Adjacent to Cheongpyeong Lake',              NOW(), NOW()),
(22, 'ja', '加平フォレストプールヴィラ',    '京畿道加平郡清平面湖畔路50',                  '清平湖隣接',                                 NOW(), NOW()),
(23, 'en', 'Itaewon Global Guesthouse',     '150 Itaewon-ro, Yongsan-gu, Seoul',           'Exit 3, Itaewon Station',                    NOW(), NOW()),
(23, 'ja', '梨泰院グローバルゲストハウス',  'ソウル市龍山区梨泰院路150',                   '梨泰院駅3番出口',                            NOW(), NOW()),
(24, 'en', 'Myeongdong Stay Guesthouse',    '30 Myeongdong-gil, Jung-gu, Seoul',           'Exit 5, Myeongdong Station',                 NOW(), NOW()),
(24, 'ja', '明洞ステイゲストハウス',        'ソウル市中区明洞キル30',                      '明洞駅5番出口',                              NOW(), NOW()),
(25, 'en', 'Haeundae Beach Guesthouse',     '100 Gunam-ro, Haeundae-gu, Busan',            '3-min walk to Haeundae Beach',              NOW(), NOW()),
(25, 'ja', '海雲台ビーチゲストハウス',      '釜山市海雲台区亀南路100',                     '海雲台ビーチ徒歩3分',                        NOW(), NOW());

-- 추가 객실 번역 (room_translation, room 9-27)
INSERT INTO room_translation (room_id, locale, name, room_type_name, created_at, updated_at) VALUES
(9,  'en', 'Standard Double',      'Standard',   NOW(), NOW()),
(9,  'ja', 'スタンダードダブル',    'スタンダード', NOW(), NOW()),
(10, 'en', 'Sea View Deluxe',      'Deluxe',     NOW(), NOW()),
(10, 'ja', 'シービューデラックス',  'デラックス', NOW(), NOW()),
(11, 'en', 'City View Double',     'Standard',   NOW(), NOW()),
(11, 'ja', 'シティビューダブル',    'スタンダード', NOW(), NOW()),
(12, 'en', 'Hanok Style Room',     'Standard',   NOW(), NOW()),
(12, 'ja', '韓屋スタイルルーム',   'スタンダード', NOW(), NOW()),
(13, 'en', 'Standard Room',        'Standard',   NOW(), NOW()),
(13, 'ja', 'スタンダードルーム',    'スタンダード', NOW(), NOW()),
(14, 'en', 'Double Room',          'Standard',   NOW(), NOW()),
(14, 'ja', 'ダブルルーム',          'スタンダード', NOW(), NOW()),
(15, 'en', 'Standard Room',        'Standard',   NOW(), NOW()),
(15, 'ja', 'スタンダードルーム',    'スタンダード', NOW(), NOW()),
(16, 'en', 'Ocean View Room',      'Standard',   NOW(), NOW()),
(16, 'ja', 'オーシャンビュールーム','スタンダード', NOW(), NOW()),
(17, 'en', 'Cliff View Deluxe',    'Deluxe',     NOW(), NOW()),
(17, 'ja', 'クリフビューデラックス','デラックス', NOW(), NOW()),
(18, 'en', 'Ocean View Suite',     'Suite',      NOW(), NOW()),
(18, 'ja', 'オーシャンビュースイート','スイート', NOW(), NOW()),
(19, 'en', 'Sea View Deluxe',      'Deluxe',     NOW(), NOW()),
(19, 'ja', 'シービューデラックス',  'デラックス', NOW(), NOW()),
(20, 'en', 'River View Room',      'Family',     NOW(), NOW()),
(20, 'ja', 'リバービュールーム',    'ファミリー', NOW(), NOW()),
(21, 'en', 'Pine Room',            'Family',     NOW(), NOW()),
(21, 'ja', '松の間',                'ファミリー', NOW(), NOW()),
(22, 'en', 'Mountain Lodge Room',  'Family',     NOW(), NOW()),
(22, 'ja', '山荘ルーム',            'ファミリー', NOW(), NOW()),
(23, 'en', 'Sunrise Villa',        'Villa',      NOW(), NOW()),
(23, 'ja', 'サンライズヴィラ',      'ヴィラ',     NOW(), NOW()),
(24, 'en', 'Forest Villa',         'Villa',      NOW(), NOW()),
(24, 'ja', 'フォレストヴィラ',      'ヴィラ',     NOW(), NOW()),
(25, 'en', '4-Person Dormitory',   'Dormitory',  NOW(), NOW()),
(25, 'ja', '4人ドミトリー',         'ドミトリー', NOW(), NOW()),
(26, 'en', '4-Person Dormitory',   'Dormitory',  NOW(), NOW()),
(26, 'ja', '4人ドミトリー',         'ドミトリー', NOW(), NOW()),
(27, 'en', 'Beach View Dormitory', 'Dormitory',  NOW(), NOW()),
(27, 'ja', 'ビーチビュードミトリー','ドミトリー', NOW(), NOW());

-- 추가 객실 옵션 번역 (room_option_translation, option 11-29)
INSERT INTO room_option_translation (room_option_id, locale, name, created_at, updated_at) VALUES
(11, 'en', 'Standard', NOW(), NOW()), (11, 'ja', 'スタンダード', NOW(), NOW()),
(12, 'en', 'Standard', NOW(), NOW()), (12, 'ja', 'スタンダード', NOW(), NOW()),
(13, 'en', 'Standard', NOW(), NOW()), (13, 'ja', 'スタンダード', NOW(), NOW()),
(14, 'en', 'Standard', NOW(), NOW()), (14, 'ja', 'スタンダード', NOW(), NOW()),
(15, 'en', 'Standard', NOW(), NOW()), (15, 'ja', 'スタンダード', NOW(), NOW()),
(16, 'en', 'Standard', NOW(), NOW()), (16, 'ja', 'スタンダード', NOW(), NOW()),
(17, 'en', 'Standard', NOW(), NOW()), (17, 'ja', 'スタンダード', NOW(), NOW()),
(18, 'en', 'Standard', NOW(), NOW()), (18, 'ja', 'スタンダード', NOW(), NOW()),
(19, 'en', 'Standard', NOW(), NOW()), (19, 'ja', 'スタンダード', NOW(), NOW()),
(20, 'en', 'Standard', NOW(), NOW()), (20, 'ja', 'スタンダード', NOW(), NOW()),
(21, 'en', 'Standard', NOW(), NOW()), (21, 'ja', 'スタンダード', NOW(), NOW()),
(22, 'en', 'Standard', NOW(), NOW()), (22, 'ja', 'スタンダード', NOW(), NOW()),
(23, 'en', 'Standard', NOW(), NOW()), (23, 'ja', 'スタンダード', NOW(), NOW()),
(24, 'en', 'Standard', NOW(), NOW()), (24, 'ja', 'スタンダード', NOW(), NOW()),
(25, 'en', 'Standard', NOW(), NOW()), (25, 'ja', 'スタンダード', NOW(), NOW()),
(26, 'en', 'Standard', NOW(), NOW()), (26, 'ja', 'スタンダード', NOW(), NOW()),
(27, 'en', 'Standard', NOW(), NOW()), (27, 'ja', 'スタンダード', NOW(), NOW()),
(28, 'en', 'Standard', NOW(), NOW()), (28, 'ja', 'スタンダード', NOW(), NOW()),
(29, 'en', 'Standard', NOW(), NOW()), (29, 'ja', 'スタンダード', NOW(), NOW());

-- time_slot_inventory: room_option_id=7 (10:00~22:00 30분 단위, 2026-04-22~2026-05-10)
INSERT INTO time_slot_inventory (room_option_id, date, slot_time, status, created_at, updated_at) VALUES
(7, '2026-04-22', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-22', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-23', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-24', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-25', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-26', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-27', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-28', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-29', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-04-30', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-01', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-02', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-03', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-04', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-05', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-06', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-07', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-08', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-09', '21:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '10:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '10:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '11:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '11:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '12:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '12:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '13:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '13:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '14:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '14:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '15:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '15:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '16:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '16:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '17:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '17:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '18:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '18:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '19:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '19:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '20:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '20:30:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '21:00:00', 'AVAILABLE', NOW(), NOW()),
(7, '2026-05-10', '21:30:00', 'AVAILABLE', NOW(), NOW());

-- 승인 대기 숙소 (관리자 시나리오 S-A1 테스트용)
INSERT INTO accommodation (id, partner_id, name, type, full_address, latitude, longitude, location_description, status, check_in_time, check_out_time, supplier_managed, created_at, updated_at)
VALUES (26, 2, '부산 비스타 호텔', 'HOTEL', '부산시 중구 중앙대로 101', 35.1028, 129.0319, '부산역 도보 5분', 'PENDING', '15:00', '11:00', false, NOW(), NOW()),
       (27, 3, '강릉 오션뷰 펜션', 'PENSION', '강원도 강릉시 강동면 해안로 77', 37.7519, 129.0750, '정동진 인근 오션뷰', 'PENDING', '15:00', '11:00', false, NOW(), NOW());
