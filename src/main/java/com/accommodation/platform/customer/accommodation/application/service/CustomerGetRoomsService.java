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
import com.accommodation.platform.core.room.adapter.out.persistence.RoomImageJpaRepository;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionTranslationJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomTranslationJpaEntity;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionTranslationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomTranslationPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
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
    private final RoomImageJpaRepository roomImageJpaRepository;
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
        Map<Long, RoomTranslationJpaEntity> roomTranslations = isKorean(locale) || roomIds.isEmpty()
                ? Map.of()
                : loadRoomTranslationPort.findByRoomIdInAndLocale(roomIds, locale).stream()
                        .collect(Collectors.toMap(RoomTranslationJpaEntity::getRoomId, t -> t));

        Map<Long, RoomOptionTranslationJpaEntity> optionTranslations = isKorean(locale) || allOptionIds.isEmpty()
                ? Map.of()
                : loadRoomOptionTranslationPort.findByRoomOptionIdInAndLocale(allOptionIds, locale).stream()
                        .collect(Collectors.toMap(RoomOptionTranslationJpaEntity::getRoomOptionId, t -> t));

        return rooms.stream()
                .map(room -> buildRoomDetail(
                        room,
                        optionsByRoomId.getOrDefault(room.getId(), List.of()),
                        roomTranslations,
                        optionTranslations,
                        checkInDate, checkOutDate,
                        accCheckInTime, accCheckOutTime))
                .toList();
    }

    private RoomDetail buildRoomDetail(Room room,
                                        List<RoomOption> options,
                                        Map<Long, RoomTranslationJpaEntity> roomTranslations,
                                        Map<Long, RoomOptionTranslationJpaEntity> optionTranslations,
                                        LocalDate checkInDate, LocalDate checkOutDate,
                                        String accCheckInTime, String accCheckOutTime) {

        RoomTranslationJpaEntity roomTr = roomTranslations.get(room.getId());
        String roomName = (roomTr != null && roomTr.getName() != null && !roomTr.getName().isBlank())
                ? roomTr.getName() : room.getName();
        String roomTypeName = (roomTr != null && roomTr.getRoomTypeName() != null && !roomTr.getRoomTypeName().isBlank())
                ? roomTr.getRoomTypeName() : room.getRoomTypeName();

        List<RoomImageInfo> roomImages = roomImageJpaRepository
                .findByRoomIdOrderByDisplayOrderAsc(room.getId()).stream()
                .map(img -> new RoomImageInfo(img.getRelativePath(), img.getDisplayOrder(), img.isPrimary()))
                .toList();

        List<OptionDetail> optionDetails = options.stream()
                .map(option -> buildOptionDetail(option, optionTranslations, checkInDate, checkOutDate,
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
                                            Map<Long, RoomOptionTranslationJpaEntity> optionTranslations,
                                            LocalDate checkInDate, LocalDate checkOutDate,
                                            String accCheckInTime, String accCheckOutTime) {

        RoomOptionTranslationJpaEntity optTr = optionTranslations.get(option.getId());
        String optionName = (optTr != null && optTr.getName() != null && !optTr.getName().isBlank())
                ? optTr.getName() : option.getName();

        Set<String> availablePriceTypes = new LinkedHashSet<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int remainingQuantity = 0;

        if (checkInDate != null && checkOutDate != null) {
            List<RoomPrice> stayPrices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                    option.getId(), PriceType.STAY, checkInDate, checkOutDate.minusDays(1));
            if (!stayPrices.isEmpty()) availablePriceTypes.add(PriceType.STAY.name());
            totalPrice = priceDomainService.calculateTotalPriceWithVat(stayPrices);

            List<RoomPrice> hourlyPrices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                    option.getId(), PriceType.HOURLY, checkInDate, checkInDate);
            if (!hourlyPrices.isEmpty()) availablePriceTypes.add(PriceType.HOURLY.name());

            List<Inventory> inventories = loadInventoryPort.findByRoomOptionIdAndDateRange(
                    option.getId(), checkInDate, checkOutDate.minusDays(1));
            remainingQuantity = inventories.stream()
                    .mapToInt(Inventory::getRemainingQuantity)
                    .min()
                    .orElse(0);
        }

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
