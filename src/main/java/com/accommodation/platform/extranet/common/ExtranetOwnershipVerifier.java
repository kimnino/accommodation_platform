package com.accommodation.platform.extranet.common;

import org.springframework.stereotype.Component;

import com.accommodation.platform.common.exception.BusinessException;
import com.accommodation.platform.common.exception.ErrorCode;
import com.accommodation.platform.core.accommodation.application.port.out.LoadAccommodationPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionPort;
import com.accommodation.platform.core.room.application.port.out.LoadRoomPort;
import com.accommodation.platform.core.room.domain.model.Room;
import com.accommodation.platform.core.room.domain.model.RoomOption;

import lombok.RequiredArgsConstructor;

/**
 * 파트너(Extranet) 채널 공통 소유권 검증.
 * 숙소/객실/객실옵션이 요청한 파트너 소유인지 확인한다.
 */
@Component
@RequiredArgsConstructor
public class ExtranetOwnershipVerifier {

    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadRoomPort loadRoomPort;
    private final LoadRoomOptionPort loadRoomOptionPort;

    /**
     * 숙소가 해당 파트너 소유인지 검증.
     */
    public void verifyAccommodationOwnership(Long accommodationId, Long partnerId) {

        loadAccommodationPort.findById(accommodationId)
                .filter(acc -> acc.getPartnerId().equals(partnerId))
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ACCOMMODATION_NOT_FOUND, "해당 숙소에 대한 접근 권한이 없습니다."));
    }

    /**
     * 객실 옵션 → 객실 → 숙소 경로로 파트너 소유 검증.
     */
    public void verifyRoomOptionOwnership(Long roomOptionId, Long partnerId) {

        RoomOption option = loadRoomOptionPort.findById(roomOptionId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ROOM_NOT_FOUND, "객실 옵션을 찾을 수 없습니다."));

        verifyRoomOwnership(option.getRoomId(), partnerId);
    }

    /**
     * 객실 → 숙소 경로로 파트너 소유 검증.
     */
    public void verifyRoomOwnership(Long roomId, Long partnerId) {

        Room room = loadRoomPort.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        verifyAccommodationOwnership(room.getAccommodationId(), partnerId);
    }
}
