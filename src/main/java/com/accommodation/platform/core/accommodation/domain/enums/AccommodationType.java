package com.accommodation.platform.core.accommodation.domain.enums;

public enum AccommodationType {
    /*
    유형 특징에 따라 코드 로직 개선과 함께 작업 가능성이 높고. 유형 추가는 빈번하지 않으므로 enum으로 관리.
    EXTERNAL: 외부 공급사(OTA 파트너) 연동 숙소. 업체(Expedia, Booking.com 등)와 무관하게 단일 값으로 구별.
              공급사별 코드(A_HOTEL, B_HOTEL 등)를 사용하지 않는 이유: 숙소 유형과 공급사 정보는 관심사가 다름.
              공급사 식별은 SupplierAccommodationMapping 테이블로 처리.
    */
    HOTEL,
    RESORT,
    PENSION,
    POOL_VILLA,
    MOTEL,
    GUEST_HOUSE,
    /** 외부 공급사 연동 숙소. 공급사 구분은 supplier_accommodation_mapping 참조. */
    EXTERNAL
}
