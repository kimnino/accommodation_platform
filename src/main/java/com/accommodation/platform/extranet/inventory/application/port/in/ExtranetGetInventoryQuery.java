package com.accommodation.platform.extranet.inventory.application.port.in;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.inventory.domain.model.Inventory;

public interface ExtranetGetInventoryQuery {

    List<Inventory> getInventory(Long roomOptionId, Long partnerId, LocalDate startDate, LocalDate endDate);
}
