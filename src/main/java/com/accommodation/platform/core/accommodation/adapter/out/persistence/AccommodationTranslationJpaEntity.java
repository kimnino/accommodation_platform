package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙소 다국어 상세 정보 테이블.
 * 하나의 숙소에 대해 언어별(locale)로 이름, 주소, 상세 정보를 관리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_translation")
public class AccommodationTranslationJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 숙소 ID (FK 미사용, 인덱스로 조인) */
    @Column(nullable = false)
    private Long accommodationId;

    /** 언어 코드 (ko, en, ja, zh 등) */
    @Column(nullable = false, length = 10)
    private String locale;

    /** 숙소명 — 언어별 번역 */
    private String name;

    /** 풀주소 (도로명/지번) — 언어별 번역 */
    private String fullAddress;

    /** 위치 관련 보충 설명 — 언어별 번역 */
    private String locationDescription;

    /** 숙소 소개 */
    @Column(columnDefinition = "TEXT")
    private String introduction;

    /** 서비스 및 부대시설 안내 */
    @Column(columnDefinition = "TEXT")
    private String serviceAndFacilities;

    /** 숙소 이용 정보 (입실/퇴실 안내 등) */
    @Column(columnDefinition = "TEXT")
    private String usageInfo;

    /** 취소 및 환불 규정 */
    @Column(columnDefinition = "TEXT")
    private String cancellationAndRefundPolicy;

    public AccommodationTranslationJpaEntity(Long accommodationId, String locale, String name,
                                              String fullAddress, String locationDescription,
                                              String introduction, String serviceAndFacilities,
                                              String usageInfo, String cancellationAndRefundPolicy) {

        this.accommodationId = accommodationId;
        this.locale = locale;
        this.name = name;
        this.fullAddress = fullAddress;
        this.locationDescription = locationDescription;
        this.introduction = introduction;
        this.serviceAndFacilities = serviceAndFacilities;
        this.usageInfo = usageInfo;
        this.cancellationAndRefundPolicy = cancellationAndRefundPolicy;
    }
}
