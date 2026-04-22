package com.accommodation.platform.customer.accommodation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.adapter.out.persistence.AccommodationTranslationJpaEntity;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.core.inventory.application.port.out.LoadInventoryPort;
import com.accommodation.platform.core.inventory.domain.model.Inventory;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.core.price.domain.service.PriceDomainService;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomImageJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomImageJpaRepository;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomOptionTranslationJpaEntity;
import com.accommodation.platform.core.room.adapter.out.persistence.RoomTranslationJpaEntity;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionTranslationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomTranslationPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.customer.accommodation.application.port.in.CustomerGetAccommodationDetailQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerGetAccommodationDetailService implements CustomerGetAccommodationDetailQuery {

    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadAccommodationTranslationPort loadAccommodationTranslationPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadRoomTranslationPort loadRoomTranslationPort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadRoomOptionTranslationPort loadRoomOptionTranslationPort;
    private final RoomImageJpaRepository roomImageJpaRepository;
    private final LoadInventoryPort loadInventoryPort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final PriceDomainService priceDomainService;

    @Override
    public AccommodationDetail getDetail(Long accommodationId,
                                          LocalDate checkInDate, LocalDate checkOutDate) {

        String locale = LocaleContextHolder.getLocale().getLanguage();

        Accommodation accommodation = loadAccommodationPort.findById(accommodationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

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
        Optional<AccommodationTranslationJpaEntity> accTranslation = isKorean(locale)
                ? Optional.empty()
                : loadAccommodationTranslationPort.findByAccommodationIdAndLocale(accommodationId, locale);

        Map<Long, RoomTranslationJpaEntity> roomTranslations = isKorean(locale) || roomIds.isEmpty()
                ? Map.of()
                : loadRoomTranslationPort.findByRoomIdInAndLocale(roomIds, locale).stream()
                        .collect(Collectors.toMap(RoomTranslationJpaEntity::getRoomId, t -> t));

        Map<Long, RoomOptionTranslationJpaEntity> optionTranslations = isKorean(locale) || allOptionIds.isEmpty()
                ? Map.of()
                : loadRoomOptionTranslationPort.findByRoomOptionIdInAndLocale(allOptionIds, locale).stream()
                        .collect(Collectors.toMap(RoomOptionTranslationJpaEntity::getRoomOptionId, t -> t));

        // 숙소 번역 적용 (없으면 기본값 fallback)
        String accName = accTranslation.map(AccommodationTranslationJpaEntity::getName)
                .filter(s -> s != null && !s.isBlank())
                .orElse(accommodation.getName());
        String fullAddress = accTranslation.map(AccommodationTranslationJpaEntity::getFullAddress)
                .filter(s -> s != null && !s.isBlank())
                .orElse(accommodation.getFullAddress());
        String locationDescription = accTranslation.map(AccommodationTranslationJpaEntity::getLocationDescription)
                .filter(s -> s != null && !s.isBlank())
                .orElse(accommodation.getLocationDescription());

        List<ImageInfo> images = accommodation.getImages().stream()
                .map(img -> new ImageInfo(
                        img.relativePath(), img.category().name(),
                        img.displayOrder(), img.isPrimary()))
                .toList();

        List<RoomWithOptions> roomWithOptionsList = rooms.stream()
                .map(room -> buildRoomWithOptions(
                        room,
                        optionsByRoomId.getOrDefault(room.getId(), List.of()),
                        roomTranslations,
                        optionTranslations,
                        checkInDate, checkOutDate,
                        accommodation))
                .toList();

        String checkInTime = accommodation.getCheckInTime() != null
                ? accommodation.getCheckInTime().toString() : null;
        String checkOutTime = accommodation.getCheckOutTime() != null
                ? accommodation.getCheckOutTime().toString() : null;

        return new AccommodationDetail(
                accommodation.getId(),
                accName,
                accommodation.getType().name(),
                accommodation.getStatus().name(),
                fullAddress,
                accommodation.getLatitude(),
                accommodation.getLongitude(),
                locationDescription,
                checkInTime,
                checkOutTime,
                accommodation.getRegionId(),
                accommodation.getTagIds(),
                accommodation.getCreatedAt(),
                accommodation.getUpdatedAt(),
                images,
                roomWithOptionsList);
    }

    private RoomWithOptions buildRoomWithOptions(Room room,
                                                  List<RoomOption> options,
                                                  Map<Long, RoomTranslationJpaEntity> roomTranslations,
                                                  Map<Long, RoomOptionTranslationJpaEntity> optionTranslations,
                                                  LocalDate checkInDate, LocalDate checkOutDate,
                                                  Accommodation accommodation) {

        RoomTranslationJpaEntity roomTr = roomTranslations.get(room.getId());
        String roomName = (roomTr != null && roomTr.getName() != null && !roomTr.getName().isBlank())
                ? roomTr.getName() : room.getName();
        String roomTypeName = (roomTr != null && roomTr.getRoomTypeName() != null && !roomTr.getRoomTypeName().isBlank())
                ? roomTr.getRoomTypeName() : room.getRoomTypeName();

        List<OptionWithPrice> optionWithPrices = options.stream()
                .map(option -> buildOptionWithPrice(option, optionTranslations, checkInDate, checkOutDate, accommodation))
                .toList();

        List<RoomImageInfo> roomImages = roomImageJpaRepository
                .findByRoomIdOrderByDisplayOrderAsc(room.getId()).stream()
                .map(img -> new RoomImageInfo(img.getRelativePath(), img.getDisplayOrder(), img.isPrimary()))
                .toList();

        return new RoomWithOptions(
                room.getId(),
                roomName,
                roomTypeName,
                room.getStandardCapacity(),
                room.getMaxCapacity(),
                room.getStatus().name(),
                roomImages,
                optionWithPrices);
    }

    private OptionWithPrice buildOptionWithPrice(RoomOption option,
                                                  Map<Long, RoomOptionTranslationJpaEntity> optionTranslations,
                                                  LocalDate checkInDate, LocalDate checkOutDate,
                                                  Accommodation accommodation) {

        RoomOptionTranslationJpaEntity optTr = optionTranslations.get(option.getId());
        String optionName = (optTr != null && optTr.getName() != null && !optTr.getName().isBlank())
                ? optTr.getName() : option.getName();

        List<RoomPrice> stayPrices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                option.getId(), PriceType.STAY, checkInDate, checkOutDate.minusDays(1));

        BigDecimal totalPrice = priceDomainService.calculateTotalPriceWithVat(stayPrices);

        List<Inventory> inventories = loadInventoryPort.findByRoomOptionIdAndDateRange(
                option.getId(), checkInDate, checkOutDate.minusDays(1));

        int minRemaining = inventories.stream()
                .mapToInt(Inventory::getRemainingQuantity)
                .min()
                .orElse(0);

        Set<String> availablePriceTypes = new LinkedHashSet<>();
        if (!stayPrices.isEmpty()) availablePriceTypes.add(PriceType.STAY.name());
        List<RoomPrice> hourlyPrices = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                option.getId(), PriceType.HOURLY, checkInDate, checkInDate);
        if (!hourlyPrices.isEmpty()) availablePriceTypes.add(PriceType.HOURLY.name());

        // 옵션 레벨 시간 우선, 없으면 숙소 기본값 사용 (레이트 체크아웃 등 옵션별 재정의 지원)
        String checkInTime = option.getCheckInTime() != null
                ? option.getCheckInTime().toString()
                : (accommodation.getCheckInTime() != null ? accommodation.getCheckInTime().toString() : null);
        String checkOutTime = option.getCheckOutTime() != null
                ? option.getCheckOutTime().toString()
                : (accommodation.getCheckOutTime() != null ? accommodation.getCheckOutTime().toString() : null);
        String hourlyStart = option.getHourlyStartTime() != null
                ? option.getHourlyStartTime().toString() : null;
        String hourlyEnd = option.getHourlyEndTime() != null
                ? option.getHourlyEndTime().toString() : null;

        return new OptionWithPrice(
                option.getId(),
                optionName,
                option.getCancellationPolicy().name(),
                totalPrice,
                option.getAdditionalPrice(),
                minRemaining,
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
