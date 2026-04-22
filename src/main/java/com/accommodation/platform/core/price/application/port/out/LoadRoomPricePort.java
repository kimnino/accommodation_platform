package com.accommodation.platform.core.price.application.port.out;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;

public interface LoadRoomPricePort {

    List<RoomPrice> findByRoomOptionIdAndDateRange(Long roomOptionId, LocalDate startDate, LocalDate endDate);

    List<RoomPrice> findByRoomOptionIdAndPriceTypeAndDateRange(
            Long roomOptionId, PriceType priceType, LocalDate startDate, LocalDate endDate);

    List<RoomPrice> findByRoomOptionIdInAndPriceTypeAndDateRange(
            List<Long> roomOptionIds, PriceType priceType, LocalDate startDate, LocalDate endDate);
}
