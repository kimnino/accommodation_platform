package com.accommodation.platform.customer.accommodation.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CustomerGetAccommodationDetailQuery {

    AccommodationDetail getDetail(Long accommodationId, LocalDate checkInDate, LocalDate checkOutDate);

    record AccommodationDetail(
            Long id,
            String name,
            String type,
            String status,
            String fullAddress,
            double latitude,
            double longitude,
            String locationDescription,
            String checkInTime,
            String checkOutTime,
            Long regionId,
            List<Long> tagIds,
            Instant createdAt,
            Instant updatedAt,
            List<ImageInfo> images,
            List<RoomWithOptions> rooms
    ) {
    }

    record ImageInfo(String relativePath, String category, int displayOrder, boolean isPrimary) {
    }

    record RoomWithOptions(
            Long roomId,
            String roomName,
            String roomTypeName,
            int standardCapacity,
            int maxCapacity,
            String status,
            List<RoomImageInfo> images,
            List<OptionWithPrice> options
    ) {
    }

    record RoomImageInfo(String relativePath, int displayOrder, boolean isPrimary) {
    }

    record OptionWithPrice(
            Long optionId,
            String optionName,
            String cancellationPolicy,
            BigDecimal totalPrice,
            BigDecimal additionalPrice,
            int remainingQuantity,
            Set<String> availablePriceTypes,
            String checkInTime,
            String checkOutTime,
            String hourlyStartTime,
            String hourlyEndTime
    ) {
    }
}
