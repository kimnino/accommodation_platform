package com.accommodation.platform.admin.supplier.adapter.in.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.supplier.application.port.in.SyncSupplierInventoryUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/supplier")
@RequiredArgsConstructor
public class AdminSupplierSyncController {

    private final SyncSupplierInventoryUseCase syncSupplierInventoryUseCase;

    @PostMapping("/{supplierCode}/sync")
    public ResponseEntity<ApiResponse<SyncSupplierInventoryUseCase.SyncResult>> sync(
            @PathVariable String supplierCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("공급사 동기화 요청: supplier={}, {}~{}", supplierCode, startDate, endDate);
        SyncSupplierInventoryUseCase.SyncResult result =
                syncSupplierInventoryUseCase.syncPricesAndInventory(supplierCode, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
