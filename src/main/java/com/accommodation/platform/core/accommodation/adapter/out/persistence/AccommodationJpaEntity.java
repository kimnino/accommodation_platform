package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙소 기본 정보 테이블.
 * 다국어 상세 정보(소개, 이용정보 등)는 AccommodationTranslationJpaEntity에서 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation")
public class AccommodationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 업체(파트너) 번호. 향후 판매자 정보 표시에도 활용
     */
    @Column(nullable = false)
    private Long partnerId;

    /**
     * 숙소명 (기본 언어)
     */
    @Column(nullable = false)
    private String name;

    /**
     * 숙소 유형 (HOTEL, RESORT, PENSION 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType type;

    /**
     * 검색/필터용 지역 ID (accommodation_region.id 참조). 유형별로 다른 지역 분류를 사용
     */
    private Long regionId;

    /**
     * 풀주소 (도로명/지번 등) — 다국어 대상
     */
    @Column(nullable = false)
    private String fullAddress;

    /**
     * 위도
     */
    private double latitude;

    /**
     * 경도
     */
    private double longitude;

    /**
     * 위치 관련 보충 설명 — 다국어 대상
     */
    private String locationDescription;

    /**
     * 숙소 상태 (PENDING → ACTIVE → SUSPENDED / CLOSED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationStatus status;

    /**
     * 체크인 시간 (현지 시간 기준)
     */
    private LocalTime checkInTime;

    /**
     * 체크아웃 시간 (현지 시간 기준)
     */
    private LocalTime checkOutTime;

    /** 외부 공급사 연동 숙소 여부 (supplier_accommodation_mapping에 매핑이 있으면 true) */
    private boolean supplierManaged;

    @OneToMany(mappedBy = "accommodationId", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AccommodationImageJpaEntity> images = new ArrayList<>();

    public AccommodationJpaEntity(Long id, Long partnerId, String name, AccommodationType type,
                                  Long regionId, String fullAddress, double latitude, double longitude,
                                  String locationDescription, AccommodationStatus status,
                                  LocalTime checkInTime, LocalTime checkOutTime) {

        this.id = id;
        this.partnerId = partnerId;
        this.name = name;
        this.type = type;
        this.regionId = regionId;
        this.fullAddress = fullAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.status = status;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.supplierManaged = false;
    }
}
