package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.accommodation.domain.enums.ModificationStatus;

import static lombok.AccessLevel.PROTECTED;

/**
 * 숙소 수정 요청 테이블.
 * 파트너가 숙소 정보 수정을 요청하면 PENDING 상태로 저장.
 * 관리자가 승인(APPROVED)하면 실제 숙소 정보에 반영, 거절(REJECTED) 시 사유와 함께 반려.
 * 수정 대상 데이터는 JSON 형태로 저장하여 유연하게 처리.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "accommodation_modification_request")
public class AccommodationModificationRequestJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 수정 대상 숙소 ID
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 요청한 파트너(업체) ID
     */
    @Column(nullable = false)
    private Long partnerId;

    /**
     * 수정 요청 상태 (PENDING → APPROVED / REJECTED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModificationStatus status;

    /**
     * 수정 요청 데이터 (JSON). 변경하려는 필드와 값을 담음
     */
    @Column(nullable = false, columnDefinition = "JSON")
    private String requestData;

    /**
     * 관리자 거절 시 사유
     */
    private String rejectionReason;

    public AccommodationModificationRequestJpaEntity(Long accommodationId, Long partnerId,
                                                     ModificationStatus status, String requestData) {

        this.accommodationId = accommodationId;
        this.partnerId = partnerId;
        this.status = status;
        this.requestData = requestData;
    }

    public void approve() {

        this.status = ModificationStatus.APPROVED;
    }

    public void reject(String reason) {

        this.status = ModificationStatus.REJECTED;
        this.rejectionReason = reason;
    }
}
