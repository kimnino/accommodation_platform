package com.accommodation.platform.extranet.price.application.port.in;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

public interface ExtranetGetPriceQuery {

    List<RoomPrice> getPrice(Long roomOptionId, Long partnerId, LocalDate startDate, LocalDate endDate);
}
