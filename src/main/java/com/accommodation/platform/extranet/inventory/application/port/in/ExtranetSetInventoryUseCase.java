package com.accommodation.platform.extranet.inventory.application.port.in;

import java.time.LocalDate;
import java.util.List;

import com.accommodation.platform.core.inventory.domain.model.Inventory;
import org.springframework.util.CollectionUtils;

public interface ExtranetSetInventoryUseCase {

    List<Inventory> setInventory(Long roomOptionId, Long partnerId, SetInventoryCommand command);

    record SetInventoryCommand(
            LocalDate startDate,
            LocalDate endDate,
            List<LocalDate> dates,
            int totalQuantity
    ) {

        /**
         * 실제 적용할 날짜 목록 반환.
         * dates가 있으면 개별 날짜 사용, 없으면 startDate~endDate 범위 사용.
         */
        public List<LocalDate> getTargetDates() {

            if (!CollectionUtils.isEmpty(dates)) {
                return dates;
            }

            List<LocalDate> range = new java.util.ArrayList<>();
            for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                range.add(d);
            }
            return range;
        }
    }
}
