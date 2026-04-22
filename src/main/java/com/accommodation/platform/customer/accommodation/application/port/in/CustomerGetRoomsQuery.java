package com.accommodation.platform.customer.accommodation.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CustomerGetRoomsQuery {

    List<RoomDetail> getRooms(Long accommodationId, LocalDate checkInDate, LocalDate checkOutDate);

    record RoomDetail(
            Long roomId,
            String roomName,
            String roomTypeName,
            int standardCapacity,
            int maxCapacity,
            String status,
            List<RoomImageInfo> images,
            List<OptionDetail> options
    ) {
    }

    record RoomImageInfo(String relativePath, int displayOrder, boolean isPrimary) {
    }

    record OptionDetail(
            Long optionId,
            String optionName,
            String cancellationPolicy,
            BigDecimal additionalPrice,
            BigDecimal totalPrice,
            int remainingQuantity,
            Set<String> availablePriceTypes,
            String checkInTime,
            String checkOutTime,
            String hourlyStartTime,
            String hourlyEndTime
    ) {
    }
}
