package com.accommodation.platform.customer.accommodation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.core.price.domain.service.PriceDomainService;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomImageJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomImageJpaRepository;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerGetAccommodationDetailService implements CustomerGetAccommodationDetailQuery {

    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final RoomImageJpaRepository roomImageJpaRepository;
    private final LoadInventoryPort loadInventoryPort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final PriceDomainService priceDomainService;

    @Override
    public AccommodationDetail getDetail(Long accommodationId,
                                          LocalDate checkInDate, LocalDate checkOutDate) {

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        List<Room> rooms = loadRoomPort.findByAccommodationId(accommodationId);

        List<RoomWithOptions> roomWithOptions = rooms.stream()
                .map(room -> buildRoomWithOptions(room, checkInDate, checkOutDate))
                .toList();

        List<ImageInfo> images = accommodation.getImages().stream()
                .map(img -> new ImageInfo(
                        img.relativePath(), img.category().name(),
                        img.displayOrder(), img.isPrimary()))
                .toList();

        return new AccommodationDetail(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getType().name(),
                accommodation.getStatus().name(),
                accommodation.getFullAddress(),
                accommodation.getLatitude(),
                accommodation.getLongitude(),
                accommodation.getLocationDescription(),
                images,
                roomWithOptions);
    }

    private RoomWithOptions buildRoomWithOptions(Room room,
                                                  LocalDate checkInDate, LocalDate checkOutDate) {

        List<RoomOption> options = loadRoomOptionPort.findByRoomId(room.getId());

        List<OptionWithPrice> optionWithPrices = options.stream()
                .map(option -> buildOptionWithPrice(option, checkInDate, checkOutDate))
                .toList();

        List<RoomImageInfo> roomImages = roomImageJpaRepository
                .findByRoomIdOrderByDisplayOrderAsc(room.getId()).stream()
                .map(img -> new RoomImageInfo(img.getRelativePath(), img.getDisplayOrder(), img.isPrimary()))
                .toList();

        return new RoomWithOptions(
                room.getId(),
                room.getName(),
                room.getRoomTypeName(),
                room.getStandardCapacity(),
                room.getMaxCapacity(),
                roomImages,
                optionWithPrices);
    }

    private OptionWithPrice buildOptionWithPrice(RoomOption option,
                                                  LocalDate checkInDate, LocalDate checkOutDate) {

        List<RoomPrice> prices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                option.getId(), PriceType.STAY, checkInDate, checkOutDate.minusDays(1));

        BigDecimal totalPrice = priceDomainService.calculateTotalPriceWithVat(prices);

        List<Inventory> inventories = loadInventoryPort.findByRoomOptionIdAndDateRange(
                option.getId(), checkInDate, checkOutDate.minusDays(1));

        int minRemaining = inventories.stream()
                .mapToInt(Inventory::getRemainingQuantity)
                .min()
                .orElse(0);

        return new OptionWithPrice(
                option.getId(),
                option.getName(),
                option.getCancellationPolicy().name(),
                totalPrice,
                option.getAdditionalPrice(),
                minRemaining);
    }
}
