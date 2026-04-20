package com.accommodation.platform.core.supplier.application.port.in;

import java.time.LocalDate;

public interface SyncSupplierInventoryUseCase {

    SyncResult syncPricesAndInventory(String supplierCode, LocalDate startDate, LocalDate endDate);

    record SyncResult(int accommodationCount, int priceCount, int inventoryCount) {
    }
}
