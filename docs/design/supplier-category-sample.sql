-- ============================================================
-- 공급사 카테고리 매핑 샘플 데이터
-- 공급사: MinhyukHouse (supplier_id = 1)
--
-- 목적:
--   외부 공급사(MinhyukHouse)가 자체적으로 정의한 카테고리 체계를
--   우리 플랫폼의 내부 태그(Tag) 시스템과 연결하는 매핑 테이블 예시.
--
-- 테이블 구조:
--   supplier_category_mapping (
--       id                BIGINT PK,
--       supplier_id       BIGINT       -- 공급사 ID
--       supplier_group    VARCHAR(100) -- 공급사의 카테고리 대분류
--       supplier_value    VARCHAR(100) -- 공급사의 카테고리 세부값
--       internal_tag_id   BIGINT       -- 우리 플랫폼의 태그 ID (nullable: PENDING/IGNORED)
--       mapping_status    VARCHAR(20)  -- MAPPED | PENDING | IGNORED
--       created_at        DATETIME(6)
--       updated_at        DATETIME(6)
--   )
-- ============================================================

-- ============================================================
-- [전제] 우리 플랫폼 태그 ID 참고 (internal_tag 테이블 기준)
-- tag_id=10: 로맨틱
-- tag_id=11: 비즈니스
-- tag_id=12: 가족여행
-- tag_id=13: 반려동물 동반
-- tag_id=14: 수영장
-- tag_id=15: 조식 포함
-- tag_id=16: 무료 주차
-- tag_id=17: 피트니스센터
-- tag_id=18: 스파·사우나
-- tag_id=19: 오션뷰
-- tag_id=20: 시티뷰
-- ============================================================


-- ============================================================
-- MAPPED: 공급사 카테고리 → 플랫폼 태그 매핑 완료 (10~15개)
-- ============================================================

INSERT INTO supplier_category_mapping
    (id, supplier_id, supplier_group, supplier_value, internal_tag_id, mapping_status, created_at, updated_at)
VALUES

-- 공급사의 '테마 > 커플' = 우리의 '로맨틱' 태그 (tag_id=10)
(1,  1, '테마',     '커플',         10, 'MAPPED', NOW(), NOW()),

-- 공급사의 '테마 > 허니문' = 우리의 '로맨틱' 태그 (tag_id=10) — 동일 태그로 통합
(2,  1, '테마',     '허니문',       10, 'MAPPED', NOW(), NOW()),

-- 공급사의 '목적 > 출장/비즈니스' = 우리의 '비즈니스' 태그 (tag_id=11)
(3,  1, '목적',     '출장/비즈니스', 11, 'MAPPED', NOW(), NOW()),

-- 공급사의 '테마 > 가족여행' = 우리의 '가족여행' 태그 (tag_id=12)
(4,  1, '테마',     '가족여행',     12, 'MAPPED', NOW(), NOW()),

-- 공급사의 '특징 > 반려동물 허용' = 우리의 '반려동물 동반' 태그 (tag_id=13)
(5,  1, '특징',     '반려동물 허용', 13, 'MAPPED', NOW(), NOW()),

-- 공급사의 '시설 > 실내수영장' = 우리의 '수영장' 태그 (tag_id=14)
(6,  1, '시설',     '실내수영장',   14, 'MAPPED', NOW(), NOW()),

-- 공급사의 '시설 > 야외풀장' = 우리의 '수영장' 태그 (tag_id=14) — 수영장으로 통합
(7,  1, '시설',     '야외풀장',     14, 'MAPPED', NOW(), NOW()),

-- 공급사의 '혜택 > 조식 포함' = 우리의 '조식 포함' 태그 (tag_id=15)
(8,  1, '혜택',     '조식 포함',    15, 'MAPPED', NOW(), NOW()),

-- 공급사의 '주차 > 무료주차 가능' = 우리의 '무료 주차' 태그 (tag_id=16)
(9,  1, '주차',     '무료주차 가능', 16, 'MAPPED', NOW(), NOW()),

-- 공급사의 '시설 > 헬스장' = 우리의 '피트니스센터' 태그 (tag_id=17)
(10, 1, '시설',     '헬스장',       17, 'MAPPED', NOW(), NOW()),

-- 공급사의 '시설 > 온천/스파' = 우리의 '스파·사우나' 태그 (tag_id=18)
(11, 1, '시설',     '온천/스파',    18, 'MAPPED', NOW(), NOW()),

-- 공급사의 '뷰 > 바다전망' = 우리의 '오션뷰' 태그 (tag_id=19)
(12, 1, '뷰',       '바다전망',     19, 'MAPPED', NOW(), NOW()),

-- 공급사의 '뷰 > 오션뷰' = 우리의 '오션뷰' 태그 (tag_id=19) — 동의어 통합
(13, 1, '뷰',       '오션뷰',       19, 'MAPPED', NOW(), NOW()),

-- 공급사의 '뷰 > 시내전망' = 우리의 '시티뷰' 태그 (tag_id=20)
(14, 1, '뷰',       '시내전망',     20, 'MAPPED', NOW(), NOW()),

-- 공급사의 '뷰 > 도심뷰' = 우리의 '시티뷰' 태그 (tag_id=20) — 동의어 통합
(15, 1, '뷰',       '도심뷰',       20, 'MAPPED', NOW(), NOW());


-- ============================================================
-- PENDING: 아직 매핑되지 않은 공급사 카테고리 (관리자 검토 필요)
-- internal_tag_id = NULL (매핑 대상 태그 미결정)
-- ============================================================

INSERT INTO supplier_category_mapping
    (id, supplier_id, supplier_group, supplier_value, internal_tag_id, mapping_status, created_at, updated_at)
VALUES

-- 공급사의 '특징 > 인스타 명소' — 우리 태그 시스템에 대응 태그 없음, 신규 태그 생성 검토 필요
(16, 1, '특징',     '인스타 명소',  NULL, 'PENDING', NOW(), NOW()),

-- 공급사의 '목적 > 워케이션' — '비즈니스'로 매핑할지, 신규 태그 생성할지 미결정
(17, 1, '목적',     '워케이션',     NULL, 'PENDING', NOW(), NOW()),

-- 공급사의 '시설 > 루프탑 바' — '바/라운지' 태그 신설 검토 중
(18, 1, '시설',     '루프탑 바',    NULL, 'PENDING', NOW(), NOW());


-- ============================================================
-- IGNORED: 플랫폼 태그와 무관하거나 의미 없는 카테고리 (매핑 제외)
-- 예: 너무 광범위하거나, 우리 서비스 방향과 맞지 않는 값
-- ============================================================

INSERT INTO supplier_category_mapping
    (id, supplier_id, supplier_group, supplier_value, internal_tag_id, mapping_status, created_at, updated_at)
VALUES

-- 공급사의 '기타 > 기타숙소' — 분류 불가, 의미 없는 catch-all 값 → 무시
(19, 1, '기타',     '기타숙소',     NULL, 'IGNORED', NOW(), NOW()),

-- 공급사의 '내부코드 > TEST_ROOM' — 공급사 테스트 데이터, 실제 서비스에 노출 불필요
(20, 1, '내부코드',  'TEST_ROOM',   NULL, 'IGNORED', NOW(), NOW());
