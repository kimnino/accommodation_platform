package com.accommodation.platform.core.inventory.application.port.out;

import java.util.List;

import com.accommodation.platform.core.inventory.domain.model.Inventory;

public interface PersistInventoryPort {

    Inventory save(Inventory inventory);

    List<Inventory> saveAll(List<Inventory> inventories);
}
