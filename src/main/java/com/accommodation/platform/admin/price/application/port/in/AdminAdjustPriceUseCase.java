package com.accommodation.platform.admin.price.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

public interface AdminAdjustPriceUseCase {

    List<RoomPrice> adjustPrice(Long roomOptionId, AdjustPriceCommand command);

    record AdjustPriceCommand(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal sellingPrice,
            boolean taxIncluded
    ) {
    }
}
