package com.accommodation.platform.core.supplier.domain.model;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.supplier.domain.enums.SupplierStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Supplier extends BaseEntity {

    private Long id;
    private String name;
    private String code;
    private String apiEndpoint;
    private SupplierStatus status;

    @Builder
    public Supplier(Long id, String name, String code, String apiEndpoint) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("공급사명은 필수입니다.");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("공급사 코드는 필수입니다.");
        }
        this.id = id;
        this.name = name;
        this.code = code;
        this.apiEndpoint = apiEndpoint;
        this.status = SupplierStatus.ACTIVE;
        initTimestamps();
    }
}
