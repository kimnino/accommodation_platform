-- ============================================
-- 샘플 데이터
-- ============================================

-- 숙소 (유형별 1개씩, 파트너 3명)
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
INSERT INTO accommodation_tag (accommodation_id, tag_id)
VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(2, 1), (2, 3), (2, 4), (2, 9), (2, 10), (2, 11),
(3, 4), (3, 22), (3, 23), (3, 24),
(4, 4), (4, 12), (4, 13), (4, 14),
(5, 4), (5, 15), (5, 16), (5, 17), (5, 18),
(6, 1), (6, 4), (6, 19), (6, 20), (6, 21);
