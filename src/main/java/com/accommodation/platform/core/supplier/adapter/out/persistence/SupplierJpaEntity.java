package com.accommodation.platform.core.supplier.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;
import com.accommodation.platform.core.supplier.domain.enums.SupplierStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * 외부 공급사 정보 테이블.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "supplier")
public class SupplierJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공급사명 */
    @Column(nullable = false)
    private String name;

    /** 공급사 코드 (고유 식별자) */
    @Column(nullable = false, unique = true)
    private String code;

    /** API 엔드포인트 */
    private String apiEndpoint;

    /** 공급사 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status;

    public SupplierJpaEntity(Long id, String name, String code, String apiEndpoint, SupplierStatus status) {

        this.id = id;
        this.name = name;
        this.code = code;
        this.apiEndpoint = apiEndpoint;
        this.status = status;
    }
}
