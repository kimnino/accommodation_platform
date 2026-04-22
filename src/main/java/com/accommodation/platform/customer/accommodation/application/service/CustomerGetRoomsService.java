package com.accommodation.platform.customer.accommodation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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
import com.accommodation.platform.core.room.application.port.out.LoadRoomImagePort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionTranslationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomTranslationPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomImage;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.core.room.domain.model.RoomOptionTranslation;
import com.accommodation.platform.core.room.domain.model.RoomTranslation;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetRoomsQuery;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerGetRoomsService implements CustomerGetRoomsQuery {

    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadRoomTranslationPort loadRoomTranslationPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadRoomOptionTranslationPort loadRoomOptionTranslationPort;
    private final LoadRoomImagePort loadRoomImagePort;
    private final LoadInventoryPort loadInventoryPort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final PriceDomainService priceDomainService;

    @Override
    public List<RoomDetail> getRooms(Long accommodationId, LocalDate checkInDate, LocalDate checkOutDate) {

        String locale = LocaleContextHolder.getLocale().getLanguage();

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        String accCheckInTime = accommodation.getCheckInTime() != null
                ? accommodation.getCheckInTime().toString() : null;
        String accCheckOutTime = accommodation.getCheckOutTime() != null
                ? accommodation.getCheckOutTime().toString() : null;

        List<Room> rooms = loadRoomPort.findByAccommodationId(accommodationId);
        List<Long> roomIds = rooms.stream().map(Room::getId).toList();

        Map<Long, List<RoomOption>> optionsByRoomId = rooms.stream()
                .collect(Collectors.toMap(
                        Room::getId,
                        room -> loadRoomOptionPort.findByRoomId(room.getId())));

        List<Long> allOptionIds = optionsByRoomId.values().stream()
                .flatMap(List::stream)
                .map(RoomOption::getId)
                .toList();

        // 번역 데이터 배치 로드 (ko면 스킵)
        Map<Long, RoomTranslation> roomTranslations = isKorean(locale) || roomIds.isEmpty()
                ? Map.of()
                : loadRoomTranslationPort.findByRoomIdInAndLocale(roomIds, locale).stream()
                        .collect(Collectors.toMap(RoomTranslation::roomId, t -> t));

        Map<Long, RoomOptionTranslation> optionTranslations = isKorean(locale) || allOptionIds.isEmpty()
                ? Map.of()
                : loadRoomOptionTranslationPort.findByRoomOptionIdInAndLocale(allOptionIds, locale).stream()
                        .collect(Collectors.toMap(RoomOptionTranslation::roomOptionId, t -> t));

        // 객실 이미지 배치 로드
        Map<Long, List<RoomImage>> roomImagesMap = roomIds.isEmpty()
                ? Map.of()
                : loadRoomImagePort.findByRoomIdIn(roomIds).stream()
                        .collect(Collectors.groupingBy(RoomImage::roomId));

        // 가격/재고 배치 로드
        Map<Long, List<RoomPrice>> stayPricesByOption = Map.of();
        Map<Long, List<RoomPrice>> hourlyPricesByOption = Map.of();
        Map<Long, List<Inventory>> inventoriesByOption = Map.of();

        if (checkInDate != null && checkOutDate != null && !allOptionIds.isEmpty()) {
            LocalDate priceEndDate = checkOutDate.minusDays(1);
            stayPricesByOption = loadRoomPricePort.findByRoomOptionIdInAndPriceTypeAndDateRange(
                    allOptionIds, PriceType.STAY, checkInDate, priceEndDate).stream()
                    .collect(Collectors.groupingBy(RoomPrice::getRoomOptionId));

            hourlyPricesByOption = loadRoomPricePort.findByRoomOptionIdInAndPriceTypeAndDateRange(
                    allOptionIds, PriceType.HOURLY, checkInDate, checkInDate).stream()
                    .collect(Collectors.groupingBy(RoomPrice::getRoomOptionId));

            inventoriesByOption = loadInventoryPort.findByRoomOptionIdInAndDateRange(
                    allOptionIds, checkInDate, priceEndDate).stream()
                    .collect(Collectors.groupingBy(Inventory::getRoomOptionId));
        }

        Map<Long, List<RoomPrice>> finalStayPrices = stayPricesByOption;
        Map<Long, List<RoomPrice>> finalHourlyPrices = hourlyPricesByOption;
        Map<Long, List<Inventory>> finalInventories = inventoriesByOption;

        return rooms.stream()
                .map(room -> buildRoomDetail(
                        room,
                        optionsByRoomId.getOrDefault(room.getId(), List.of()),
                        roomTranslations,
                        optionTranslations,
                        roomImagesMap,
                        finalStayPrices,
                        finalHourlyPrices,
                        finalInventories,
                        accCheckInTime, accCheckOutTime))
                .toList();
    }

    private RoomDetail buildRoomDetail(Room room,
                                        List<RoomOption> options,
                                        Map<Long, RoomTranslation> roomTranslations,
                                        Map<Long, RoomOptionTranslation> optionTranslations,
                                        Map<Long, List<RoomImage>> roomImagesMap,
                                        Map<Long, List<RoomPrice>> stayPricesByOption,
                                        Map<Long, List<RoomPrice>> hourlyPricesByOption,
                                        Map<Long, List<Inventory>> inventoriesByOption,
                                        String accCheckInTime, String accCheckOutTime) {

        RoomTranslation roomTr = roomTranslations.get(room.getId());
        String roomName = (roomTr != null && roomTr.name() != null && !roomTr.name().isBlank())
                ? roomTr.name() : room.getName();
        String roomTypeName = (roomTr != null && roomTr.roomTypeName() != null && !roomTr.roomTypeName().isBlank())
                ? roomTr.roomTypeName() : room.getRoomTypeName();

        List<RoomImageInfo> roomImages = roomImagesMap.getOrDefault(room.getId(), List.of()).stream()
                .map(img -> new RoomImageInfo(img.relativePath(), img.displayOrder(), img.isPrimary()))
                .toList();

        List<OptionDetail> optionDetails = options.stream()
                .map(option -> buildOptionDetail(option, optionTranslations,
                        stayPricesByOption, hourlyPricesByOption, inventoriesByOption,
                        accCheckInTime, accCheckOutTime))
                .toList();

        return new RoomDetail(
                room.getId(),
                roomName,
                roomTypeName,
                room.getStandardCapacity(),
                room.getMaxCapacity(),
                room.getStatus().name(),
                roomImages,
                optionDetails);
    }

    private OptionDetail buildOptionDetail(RoomOption option,
                                            Map<Long, RoomOptionTranslation> optionTranslations,
                                            Map<Long, List<RoomPrice>> stayPricesByOption,
                                            Map<Long, List<RoomPrice>> hourlyPricesByOption,
                                            Map<Long, List<Inventory>> inventoriesByOption,
                                            String accCheckInTime, String accCheckOutTime) {

        RoomOptionTranslation optTr = optionTranslations.get(option.getId());
        String optionName = (optTr != null && optTr.name() != null && !optTr.name().isBlank())
                ? optTr.name() : option.getName();

        Set<String> availablePriceTypes = new LinkedHashSet<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int remainingQuantity = 0;

        List<RoomPrice> stayPrices = stayPricesByOption.getOrDefault(option.getId(), List.of());
        if (!stayPrices.isEmpty()) availablePriceTypes.add(PriceType.STAY.name());
        totalPrice = priceDomainService.calculateTotalPriceWithVat(stayPrices);

        List<RoomPrice> hourlyPrices = hourlyPricesByOption.getOrDefault(option.getId(), List.of());
        if (!hourlyPrices.isEmpty()) availablePriceTypes.add(PriceType.HOURLY.name());

        List<Inventory> inventories = inventoriesByOption.getOrDefault(option.getId(), List.of());
        remainingQuantity = inventories.stream()
                .mapToInt(Inventory::getRemainingQuantity)
                .min()
                .orElse(0);

        // 옵션 레벨 시간 우선, 없으면 숙소 기본값 사용 (레이트 체크아웃 등 옵션별 재정의 지원)
        String checkInTime = option.getCheckInTime() != null
                ? option.getCheckInTime().toString() : accCheckInTime;
        String checkOutTime = option.getCheckOutTime() != null
                ? option.getCheckOutTime().toString() : accCheckOutTime;
        String hourlyStart = option.getHourlyStartTime() != null
                ? option.getHourlyStartTime().toString() : null;
        String hourlyEnd = option.getHourlyEndTime() != null
                ? option.getHourlyEndTime().toString() : null;

        return new OptionDetail(
                option.getId(),
                optionName,
                option.getCancellationPolicy().name(),
                option.getAdditionalPrice(),
                totalPrice,
                remainingQuantity,
                availablePriceTypes,
                checkInTime,
                checkOutTime,
                hourlyStart,
                hourlyEnd);
    }

    private boolean isKorean(String locale) {

        return "ko".equals(locale);
    }
}
