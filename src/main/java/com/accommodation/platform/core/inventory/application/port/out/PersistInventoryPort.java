package com.accommodation.platform.core.inventory.application.port.out;

import java.util.List;

import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.inventory.domain.model.TimeSlotInventory;

public interface PersistInventoryPort {

    Inventory save(Inventory inventory);

    List<Inventory> saveAll(List<Inventory> inventories);

    void deleteByRoomOptionId(Long roomOptionId);

    List<TimeSlotInventory> saveAllTimeSlots(List<TimeSlotInventory> timeSlots);
}
