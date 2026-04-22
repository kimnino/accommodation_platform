package com.accommodation.platform.extranet.price.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.extranet.common.ExtranetOwnershipVerifier;
import com.accommodation.platform.extranet.price.application.port.in.ExtranetSetPriceUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtranetSetPriceService implements ExtranetSetPriceUseCase {

    private final PersistRoomPricePort persistRoomPricePort;
    private final LoadRoomPricePort loadRoomPricePort;
    private final ExtranetOwnershipVerifier ownershipVerifier;

    @Override
    public List<RoomPrice> setPrice(Long roomOptionId, Long partnerId, SetPriceCommand command) {

        ownershipVerifier.verifyRoomOptionOwnership(roomOptionId, partnerId);

        List<RoomPrice> prices = new ArrayList<>();
        for (LocalDate date : command.getTargetDates()) {

            List<RoomPrice> existing = loadRoomPricePort.findByRoomOptionIdAndPriceTypeAndDateRange(
                    roomOptionId, command.priceType(), date, date);

            if (!existing.isEmpty()) {
                RoomPrice price = existing.getFirst();
                price.updatePrice(command.basePrice(), command.sellingPrice(), command.taxIncluded());
                prices.add(price);
            } else {
                RoomPrice price = RoomPrice.builder()
                        .roomOptionId(roomOptionId)
                        .date(date)
                        .priceType(command.priceType())
                        .basePrice(command.basePrice())
                        .sellingPrice(command.sellingPrice())
                        .taxIncluded(command.taxIncluded())
                        .build();
                prices.add(price);
            }
        }

        return persistRoomPricePort.saveAll(prices);
    }
}
