package com.accommodation.platform.extranet.price.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;

public interface ExtranetSetPriceUseCase {

    List<RoomPrice> setPrice(Long roomOptionId, Long partnerId, SetPriceCommand command);

    record SetPriceCommand(
            LocalDate startDate,
            LocalDate endDate,
            List<LocalDate> dates,
            PriceType priceType,
            BigDecimal basePrice,
            BigDecimal sellingPrice,
            boolean taxIncluded
    ) {

        public List<LocalDate> getTargetDates() {

            if (dates != null && !dates.isEmpty()) {
                return dates;
            }

            List<LocalDate> range = new ArrayList<>();
            for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                range.add(d);
            }
            return range;
        }
    }
}
