package com.accommodation.platform.admin.price.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.admin.price.application.port.in.AdminAdjustPriceUseCase;
import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.price.domain.model.RoomPrice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAdjustPriceService implements AdminAdjustPriceUseCase {

    private final PersistRoomPricePort persistRoomPricePort;
    private final LoadRoomPricePort loadRoomPricePort;

    @Override
    public List<RoomPrice> adjustPrice(Long roomOptionId, AdjustPriceCommand command) {

        List<RoomPrice> prices = loadRoomPricePort.findByRoomOptionIdAndDateRange(
                roomOptionId, command.startDate(), command.endDate());

        for (RoomPrice price : prices) {
            price.updatePrice(price.getBasePrice(), command.sellingPrice(), command.taxIncluded());
        }

        return persistRoomPricePort.saveAll(prices);
    }
}
