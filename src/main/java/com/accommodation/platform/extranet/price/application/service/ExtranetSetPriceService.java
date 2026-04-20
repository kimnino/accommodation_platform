package com.accommodation.platform.extranet.price.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;
import com.accommodation.platform.extranet.price.application.port.in.ExtranetSetPriceUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetSetPriceService implements ExtranetSetPriceUseCase {

    private final PersistRoomPricePort persistRoomPricePort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final LoadRoomOptionPort loadRoomOptionPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<RoomPrice> setPrice(Long roomOptionId, Long partnerId, SetPriceCommand command) {

        verifyOwnership(roomOptionId, partnerId);

        List<RoomPrice> prices = new ArrayList<>();
        for (LocalDate date : command.getTargetDates()) {

            PriceType priceType = PriceType.valueOf(command.priceType());
            List<RoomPrice> existing = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                    roomOptionId, priceType, date, date);

            if (!existing.isEmpty()) {
                RoomPrice price = existing.getFirst();
                price.updatePrice(command.basePrice(), command.sellingPrice(), command.taxIncluded());
                prices.add(price);
            } else {
                RoomPrice price = RoomPrice.builder()
                        .roomOptionId(roomOptionId)
                        .date(date)
                        .priceType(priceType)
                        .basePrice(command.basePrice())
                        .sellingPrice(command.sellingPrice())
                        .taxIncluded(command.taxIncluded())
                        .build();
                prices.add(price);
            }
        }

        return persistRoomPricePort.saveAll(prices);
    }

    private void verifyOwnership(Long roomOptionId, Long partnerId) {

        RoomOption option = loadRoomOptionPort.findById(roomOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND, "객실 옵션을 찾을 수 없습니다."));

        Room room = loadRoomPort.findById(option.getRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        loadAccommodationPort.findById(room.getAccommodationId())
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));
    }
}
