package com.accommodation.platform.core.price.application.port.out;

import java.util.List;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

public interface PersistRoomPricePort {

    RoomPrice save(RoomPrice roomPrice);

    List<RoomPrice> saveAll(List<RoomPrice> roomPrices);

    void deleteByRoomOptionId(Long roomOptionId);
}
