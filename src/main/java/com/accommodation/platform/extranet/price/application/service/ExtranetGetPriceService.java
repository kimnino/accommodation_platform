package com.accommodation.platform.extranet.price.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.domain.model.RoomPrice;
import com.accommodation.platform.extranet.common.ExtranetOwnershipVerifier;
import com.accommodation.platform.extranet.price.application.port.in.ExtranetGetPriceQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtranetGetPriceService implements ExtranetGetPriceQuery {

    private final LoadRoomPricePort loadRoomPricePort;
    private final ExtranetOwnershipVerifier ownershipVerifier;

    @Override
    public List<RoomPrice> getPrice(Long roomOptionId, Long partnerId,
                                     LocalDate startDate, LocalDate endDate) {

        ownershipVerifier.verifyRoomOptionOwnership(roomOptionId, partnerId);

        return loadRoomPricePort.findByRoomOptionIdAndDateRange(roomOptionId, startDate, endDate);
    }
}
