-- ============================================================
-- OTA Accommodation Platform — Database Schema
-- ============================================================
-- 규칙
--   · FK 제약 미사용 (실무 스타일) — 조인 컬럼에 인덱스로 대체
--   · Soft Delete: is_deleted + deleted_at
--   · 모든 테이블: created_at, updated_at
--   · 금액: DECIMAL(12,2) — BigDecimal 매핑
--   · 타임스탬프: DATETIME(6) — Instant(UTC) 매핑
-- ============================================================


-- ────────────────────────────────────────────────────────────
-- 1. MEMBER (회원)  [설계 완료 / 유스케이스 미구현]
-- ────────────────────────────────────────────────────────────
CREATE TABLE member (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)    NOT NULL,
    phone       VARCHAR(20)     NOT NULL,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    role        VARCHAR(20)     NOT NULL,   -- CUSTOMER | PARTNER | ADMIN
    status      VARCHAR(20)     NOT NULL,   -- ACTIVE | SUSPENDED | WITHDRAWN
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0,
    deleted_at  DATETIME(6),
    created_at  DATETIME(6)     NOT NULL,
    updated_at  DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_member_email (email)
);


-- ────────────────────────────────────────────────────────────
-- 2. ACCOMMODATION (숙소)
-- ────────────────────────────────────────────────────────────
CREATE TABLE accommodation (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    partner_id           BIGINT          NOT NULL,   -- member.id (PARTNER 역할)
    name                 VARCHAR(255)    NOT NULL,
    type                 VARCHAR(30)     NOT NULL,   -- HOTEL | RESORT | GUEST_HOUSE | MOTEL | PENSION | POOL_VILLA
    region_id            BIGINT,                     -- accommodation_region.id
    full_address         VARCHAR(500)    NOT NULL,
    latitude             DOUBLE          NOT NULL,
    longitude            DOUBLE          NOT NULL,
    location_description VARCHAR(1000),
    status               VARCHAR(30)     NOT NULL,   -- PENDING_APPROVAL | ACTIVE | SUSPENDED | CLOSED
    check_in_time        TIME,
    check_out_time       TIME,
    supplier_managed     TINYINT(1)      NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_acc_partner_id   (partner_id),
    INDEX idx_acc_region_id    (region_id),
    INDEX idx_acc_type_status  (type, status)
);

-- 숙소 이미지
CREATE TABLE accommodation_image (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_id BIGINT          NOT NULL,
    relative_path    VARCHAR(500)    NOT NULL,   -- /accommodation/exterior/xxx.png
    category         VARCHAR(30)     NOT NULL,   -- EXTERIOR | LOBBY | ROOM | POOL | RESTAURANT | FACILITY
    display_order    INT             NOT NULL DEFAULT 0,
    is_primary       TINYINT(1)      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_acc_image_accommodation_id (accommodation_id)
);

-- 숙소 다국어 번역
CREATE TABLE accommodation_translation (
    id                          BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_id            BIGINT          NOT NULL,
    locale                      VARCHAR(10)     NOT NULL,   -- ko | en | ja
    name                        VARCHAR(255),
    full_address                VARCHAR(500),
    location_description        VARCHAR(1000),
    introduction                TEXT,
    service_and_facilities      TEXT,
    usage_info                  TEXT,
    cancellation_and_refund_policy TEXT,
    created_at                  DATETIME(6)     NOT NULL,
    updated_at                  DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_acc_translation (accommodation_id, locale),
    INDEX idx_acc_translation_acc_id (accommodation_id)
);

-- 숙소별 지원 언어
CREATE TABLE accommodation_supported_locale (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    accommodation_id BIGINT      NOT NULL,
    locale           VARCHAR(10) NOT NULL,   -- ko | en | ja
    PRIMARY KEY (id),
    UNIQUE KEY uk_acc_locale (accommodation_id, locale)
);

-- 숙소 수정 요청 (파트너 수정 → 관리자 승인 후 반영)
CREATE TABLE accommodation_modification_request (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_id BIGINT          NOT NULL,
    partner_id       BIGINT          NOT NULL,
    status           VARCHAR(20)     NOT NULL,   -- PENDING | APPROVED | REJECTED
    request_data     JSON            NOT NULL,   -- UpdateAccommodationCommand 직렬화
    rejection_reason VARCHAR(1000),
    created_at       DATETIME(6)     NOT NULL,
    updated_at       DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_mod_req_accommodation_id (accommodation_id),
    INDEX idx_mod_req_status           (status)
);

-- 숙소 지역 마스터 (유형별 계층 구조)
CREATE TABLE accommodation_region (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_type   VARCHAR(30)     NOT NULL,
    region_name          VARCHAR(100)    NOT NULL,
    parent_id            BIGINT,                     -- 상위 지역 (null = 최상위)
    sort_order           INT             NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_region_type_parent (accommodation_type, parent_id)
);

-- 대실 운영 설정
CREATE TABLE accommodation_hourly_setting (
    id                      BIGINT  NOT NULL AUTO_INCREMENT,
    accommodation_id        BIGINT  NOT NULL UNIQUE,
    operating_start_time    TIME    NOT NULL,   -- 대실 운영 시작 (예: 10:00)
    operating_end_time      TIME    NOT NULL,   -- 대실 운영 종료 (예: 22:00)
    usage_duration_minutes  INT     NOT NULL,   -- 1회 이용 시간 (분)
    buffer_minutes          INT     NOT NULL,   -- 청소/정비 시간 (분)
    slot_unit_minutes       INT     NOT NULL,   -- 슬롯 단위 (30 or 60)
    created_at              DATETIME(6) NOT NULL,
    updated_at              DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_hourly_setting_acc_id (accommodation_id)
);


-- ────────────────────────────────────────────────────────────
-- 3. ROOM (객실)
-- ────────────────────────────────────────────────────────────
CREATE TABLE room (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    accommodation_id  BIGINT          NOT NULL,
    name              VARCHAR(255)    NOT NULL,
    room_type_name    VARCHAR(100),
    standard_capacity INT             NOT NULL DEFAULT 2,
    max_capacity      INT             NOT NULL DEFAULT 4,
    status            VARCHAR(20)     NOT NULL,   -- ACTIVE | INACTIVE
    created_at        DATETIME(6)     NOT NULL,
    updated_at        DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_room_accommodation_id (accommodation_id)
);

-- 객실 이미지
CREATE TABLE room_image (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    room_id       BIGINT          NOT NULL,
    relative_path VARCHAR(500)    NOT NULL,
    display_order INT             NOT NULL DEFAULT 0,
    is_primary    TINYINT(1)      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_room_image_room_id (room_id)
);

-- 객실 다국어 번역
CREATE TABLE room_translation (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    room_id        BIGINT      NOT NULL,
    locale         VARCHAR(10) NOT NULL,
    name           VARCHAR(255),
    room_type_name VARCHAR(100),
    created_at     DATETIME(6) NOT NULL,
    updated_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_translation (room_id, locale),
    INDEX idx_room_translation_room_id (room_id)
);

-- 객실 옵션 (같은 객실의 상품 분기: 조식 포함/미포함, 대실 등)
CREATE TABLE room_option (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    room_id             BIGINT          NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    cancellation_policy VARCHAR(30)     NOT NULL,   -- FREE_CANCELLATION | NON_REFUNDABLE | PARTIAL_REFUND
    additional_price    DECIMAL(12,2)   NOT NULL DEFAULT 0,
    hourly_start_time   TIME,           -- 대실 가능 시작 시간 (null = 제한 없음)
    hourly_end_time     TIME,           -- 대실 가능 종료 시간
    check_in_time       TIME,           -- 옵션별 체크인 재정의 (null = 숙소 기본값)
    check_out_time      TIME,           -- 옵션별 체크아웃 재정의 (null = 숙소 기본값, 레이트 체크아웃 등)
    created_at          DATETIME(6)     NOT NULL,
    updated_at          DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_room_option_room_id (room_id)
);

-- 객실 옵션 다국어 번역
CREATE TABLE room_option_translation (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    room_option_id BIGINT      NOT NULL,
    locale         VARCHAR(10) NOT NULL,
    name           VARCHAR(255) NOT NULL,
    created_at     DATETIME(6) NOT NULL,
    updated_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_option_translation (room_option_id, locale),
    INDEX idx_room_opt_trans_option_id (room_option_id)
);


-- ────────────────────────────────────────────────────────────
-- 4. INVENTORY (재고)
-- ────────────────────────────────────────────────────────────

-- 숙박 재고 (날짜별 객실 수)
CREATE TABLE inventory (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    room_option_id     BIGINT      NOT NULL,
    date               DATE        NOT NULL,
    total_quantity     INT         NOT NULL DEFAULT 0,
    remaining_quantity INT         NOT NULL DEFAULT 0,
    status             VARCHAR(20) NOT NULL,   -- AVAILABLE | SOLD_OUT
    created_at         DATETIME(6) NOT NULL,
    updated_at         DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory (room_option_id, date),
    INDEX idx_inventory_option_date (room_option_id, date)
);

-- 대실 시간 슬롯 재고 (30분 단위 블록)
-- slotTime이 14:00이면 14:00~14:30 구간을 의미
CREATE TABLE time_slot_inventory (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    room_option_id BIGINT      NOT NULL,
    date           DATE        NOT NULL,
    slot_time      TIME        NOT NULL,   -- 슬롯 시작 시간 (30분 단위)
    status         VARCHAR(20) NOT NULL,   -- AVAILABLE | OCCUPIED | BLOCKED
    created_at     DATETIME(6) NOT NULL,
    updated_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_time_slot (room_option_id, date, slot_time),
    INDEX idx_time_slot_option_date (room_option_id, date)
);


-- ────────────────────────────────────────────────────────────
-- 5. PRICE (요금)
-- ────────────────────────────────────────────────────────────
CREATE TABLE room_price (
    id             BIGINT          NOT NULL AUTO_INCREMENT,
    room_option_id BIGINT          NOT NULL,
    date           DATE            NOT NULL,
    price_type     VARCHAR(20)     NOT NULL,           -- STAY | HOURLY
    base_price     DECIMAL(12,2)   NOT NULL,
    selling_price  DECIMAL(12,2)   NOT NULL,
    tax_included   TINYINT(1)      NOT NULL DEFAULT 1,
    created_at     DATETIME(6)     NOT NULL,
    updated_at     DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_price (room_option_id, date, price_type),
    INDEX idx_room_price_option_type_date (room_option_id, price_type, date)
);


-- ────────────────────────────────────────────────────────────
-- 6. RESERVATION (예약)
-- ────────────────────────────────────────────────────────────
CREATE TABLE reservation (
    id                     BIGINT          NOT NULL AUTO_INCREMENT,
    reservation_number     VARCHAR(50)     NOT NULL UNIQUE,   -- RSV-{timestamp}-{random}
    reservation_request_id VARCHAR(100)    UNIQUE,             -- 멱등성 키 (클라이언트 UUID)
    member_id              BIGINT          NOT NULL,
    accommodation_id       BIGINT          NOT NULL,
    room_option_id         BIGINT          NOT NULL,
    reservation_type       VARCHAR(10)     NOT NULL,           -- STAY | HOURLY
    check_in_date          DATE            NOT NULL,
    check_out_date         DATE,                               -- 대실은 null
    hourly_start_time      TIME,                               -- 대실 시작 시간
    hourly_usage_minutes   INT             NOT NULL DEFAULT 0, -- 대실 이용 시간 (분)
    guest_name             VARCHAR(100)    NOT NULL,
    guest_phone            VARCHAR(20)     NOT NULL,
    guest_email            VARCHAR(255),
    total_price            DECIMAL(12,2)   NOT NULL,
    status                 VARCHAR(20)     NOT NULL,           -- PAYMENT_WAITING | CONFIRMED | CHECKED_IN | CHECKED_OUT | CANCELLED
    hold_expired_at        DATETIME(6)     NOT NULL,           -- 결제 대기 만료 시각
    is_deleted             TINYINT(1)      NOT NULL DEFAULT 0,
    deleted_at             DATETIME(6),
    created_at             DATETIME(6)     NOT NULL,
    updated_at             DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_reservation_member_id        (member_id),
    INDEX idx_reservation_accommodation_id (accommodation_id),
    INDEX idx_reservation_option_id        (room_option_id),
    INDEX idx_reservation_status           (status),
    INDEX idx_reservation_hold_expired     (hold_expired_at)
);


-- ────────────────────────────────────────────────────────────
-- 7. PAYMENT (결제)  [설계 완료 / 유스케이스 미구현]
-- ────────────────────────────────────────────────────────────
CREATE TABLE payment (
    id                 BIGINT          NOT NULL AUTO_INCREMENT,
    reservation_id     BIGINT          NOT NULL,
    amount             DECIMAL(19,4)   NOT NULL,
    payment_method     VARCHAR(30)     NOT NULL,   -- CARD | KAKAO_PAY | NAVER_PAY | TOSS | BANK_TRANSFER
    status             VARCHAR(20)     NOT NULL,   -- PENDING | COMPLETED | REFUNDED | FAILED
    pg_transaction_id  VARCHAR(200),               -- PG사 거래 ID
    paid_at            DATETIME(6),
    cancelled_at       DATETIME(6),
    is_deleted         TINYINT(1)      NOT NULL DEFAULT 0,
    deleted_at         DATETIME(6),
    created_at         DATETIME(6)     NOT NULL,
    updated_at         DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_payment_reservation_id (reservation_id),
    INDEX idx_payment_status         (status)
);


-- ────────────────────────────────────────────────────────────
-- 8. REVIEW (리뷰)  [설계 완료 / 유스케이스 미구현]
-- ────────────────────────────────────────────────────────────
CREATE TABLE review (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    reservation_id   BIGINT          NOT NULL UNIQUE,   -- 예약 1건당 리뷰 1개
    member_id        BIGINT          NOT NULL,
    accommodation_id BIGINT          NOT NULL,
    rating           DECIMAL(3,1)    NOT NULL,           -- 1.0 ~ 5.0
    content          TEXT            NOT NULL,
    is_visible       TINYINT(1)      NOT NULL DEFAULT 1,
    is_deleted       TINYINT(1)      NOT NULL DEFAULT 0,
    deleted_at       DATETIME(6),
    created_at       DATETIME(6)     NOT NULL,
    updated_at       DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_review_accommodation_id (accommodation_id),
    INDEX idx_review_member_id        (member_id)
);

CREATE TABLE review_image (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    review_id     BIGINT          NOT NULL,
    relative_path VARCHAR(500)    NOT NULL,
    display_order INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_review_image_review_id (review_id)
);


-- ────────────────────────────────────────────────────────────
-- 9. COUPON (쿠폰)  [설계 완료 / 유스케이스 미구현]
-- ────────────────────────────────────────────────────────────
CREATE TABLE coupon (
    id                       BIGINT          NOT NULL AUTO_INCREMENT,
    code                     VARCHAR(50)     NOT NULL UNIQUE,
    discount_type            VARCHAR(20)     NOT NULL,   -- PERCENTAGE | FIXED_AMOUNT
    discount_amount          DECIMAL(19,4)   NOT NULL,
    minimum_order_amount     DECIMAL(19,4)   NOT NULL DEFAULT 0,
    maximum_discount_amount  DECIMAL(19,4),              -- 퍼센트 할인 최대 캡
    valid_from               DATETIME(6)     NOT NULL,
    valid_to                 DATETIME(6)     NOT NULL,
    usage_limit              INT             NOT NULL DEFAULT 0,   -- 0 = 무제한
    used_count               INT             NOT NULL DEFAULT 0,
    is_active                TINYINT(1)      NOT NULL DEFAULT 1,
    is_deleted               TINYINT(1)      NOT NULL DEFAULT 0,
    deleted_at               DATETIME(6),
    created_at               DATETIME(6)     NOT NULL,
    updated_at               DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_coupon_code     (code),
    INDEX idx_coupon_valid    (valid_from, valid_to)
);

-- 회원 쿠폰 보유
CREATE TABLE member_coupon (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    member_id  BIGINT      NOT NULL,
    coupon_id  BIGINT      NOT NULL,
    is_used    TINYINT(1)  NOT NULL DEFAULT 0,
    used_at    DATETIME(6),
    is_deleted TINYINT(1)  NOT NULL DEFAULT 0,
    deleted_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_member_coupon_member_id (member_id),
    INDEX idx_member_coupon_coupon_id (coupon_id)
);

-- 쿠폰 숙소 적용 제한 (특정 숙소에만 사용 가능한 쿠폰)
CREATE TABLE coupon_accommodation_mapping (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    coupon_id        BIGINT      NOT NULL,
    accommodation_id BIGINT      NOT NULL,
    is_deleted       TINYINT(1)  NOT NULL DEFAULT 0,
    deleted_at       DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_coupon_acc (coupon_id, accommodation_id)
);


-- ────────────────────────────────────────────────────────────
-- 10. TAG (동적 태그 시스템)
-- ────────────────────────────────────────────────────────────

-- 태그 그룹 (숙소유형, 시설, 테마 등)
CREATE TABLE tag_group (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    supplier_id   BIGINT       NOT NULL DEFAULT 0,   -- 0 = 플랫폼 자체, >0 = 공급사 전용
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_tag_group_supplier_id (supplier_id)
);

CREATE TABLE tag_group_translation (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    tag_group_id BIGINT       NOT NULL,
    locale       VARCHAR(10)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_group_translation (tag_group_id, locale)
);

-- 태그
CREATE TABLE tag (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    tag_group_id  BIGINT       NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_tag_group_id (tag_group_id)
);

CREATE TABLE tag_translation (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    tag_id     BIGINT       NOT NULL,
    locale     VARCHAR(10)  NOT NULL,
    name       VARCHAR(100) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_translation (tag_id, locale)
);

-- 숙소-태그 매핑
CREATE TABLE accommodation_tag (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    accommodation_id BIGINT NOT NULL,
    tag_id           BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_accommodation_tag (accommodation_id, tag_id),
    INDEX idx_accommodation_tag_tag_id (tag_id)
);

-- 객실-태그 매핑
CREATE TABLE room_tag (
    id      BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_tag (room_id, tag_id)
);


-- ────────────────────────────────────────────────────────────
-- 11. SUPPLIER (외부 공급사 연동)
-- ────────────────────────────────────────────────────────────
CREATE TABLE supplier (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    code         VARCHAR(50)  NOT NULL UNIQUE,   -- MINHYUK_HOUSE, EXPEDIA 등
    api_endpoint VARCHAR(500),
    is_active    TINYINT(1)   NOT NULL DEFAULT 1,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
);

-- 외부 공급사 숙소 ↔ 플랫폼 숙소 매핑
CREATE TABLE supplier_accommodation_mapping (
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    supplier_id               BIGINT       NOT NULL,
    external_accommodation_id VARCHAR(200) NOT NULL,
    accommodation_id          BIGINT       NOT NULL,
    live_price_sync           TINYINT(1)   NOT NULL DEFAULT 0,
    created_at                DATETIME(6)  NOT NULL,
    updated_at                DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_acc (supplier_id, external_accommodation_id),
    INDEX idx_supplier_acc_mapping_acc_id (accommodation_id)
);

-- 외부 공급사 카테고리 ↔ 플랫폼 태그 매핑
CREATE TABLE supplier_category_mapping (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    supplier_id      BIGINT       NOT NULL,
    supplier_group   VARCHAR(200) NOT NULL,   -- 공급사 카테고리 그룹
    supplier_value   VARCHAR(200) NOT NULL,   -- 공급사 카테고리 값
    internal_tag_id  BIGINT,                  -- 매핑된 플랫폼 tag.id
    is_active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_category (supplier_id, supplier_group, supplier_value),
    INDEX idx_supplier_cat_tag_id (internal_tag_id)
);

-- 외부 공급사 객실 ↔ 플랫폼 객실 매핑
CREATE TABLE supplier_room_mapping (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    supplier_id      BIGINT       NOT NULL,
    external_room_id VARCHAR(200) NOT NULL,
    room_id          BIGINT       NOT NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_room (supplier_id, external_room_id),
    INDEX idx_supplier_room_mapping_room_id (room_id)
);

-- 외부 공급사 옵션 ↔ 플랫폼 옵션 매핑
CREATE TABLE supplier_room_option_mapping (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    supplier_id            BIGINT       NOT NULL,
    external_room_option_id VARCHAR(200) NOT NULL,
    room_option_id         BIGINT       NOT NULL,
    created_at             DATETIME(6)  NOT NULL,
    updated_at             DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_room_option (supplier_id, external_room_option_id),
    INDEX idx_supplier_opt_mapping_option_id (room_option_id)
);


-- ────────────────────────────────────────────────────────────
-- 12. WISHLIST (찜)  [설계 완료 / 유스케이스 미구현]
-- ────────────────────────────────────────────────────────────
CREATE TABLE wishlist (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    member_id        BIGINT      NOT NULL,
    accommodation_id BIGINT      NOT NULL,
    is_deleted       TINYINT(1)  NOT NULL DEFAULT 0,
    deleted_at       DATETIME(6),
    created_at       DATETIME(6) NOT NULL,
    updated_at       DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_wishlist (member_id, accommodation_id),
    INDEX idx_wishlist_member_id (member_id)
);
