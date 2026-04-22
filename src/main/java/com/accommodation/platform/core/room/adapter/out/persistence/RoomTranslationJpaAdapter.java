package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.room.application.port.out.LoadRoomTranslationPort;
import com.accommodation.platform.core.room.domain.model.RoomTranslation;

@Repository
@RequiredArgsConstructor
public class RoomTranslationJpaAdapter implements LoadRoomTranslationPort {

    private final RoomTranslationJpaRepository jpaRepository;

    @Override
    public Optional<RoomTranslation> findByRoomIdAndLocale(Long roomId, String locale) {

        return jpaRepository.findByRoomIdAndLocale(roomId, locale)
                .map(this::toRecord);
    }

    @Override
    public List<RoomTranslation> findByRoomIdInAndLocale(List<Long> roomIds, String locale) {

        return jpaRepository.findByRoomIdInAndLocale(roomIds, locale).stream()
                .map(this::toRecord)
                .toList();
    }

    private RoomTranslation toRecord(RoomTranslationJpaEntity entity) {

        return new RoomTranslation(
                entity.getRoomId(),
                entity.getLocale(),
                entity.getName(),
                entity.getRoomTypeName());
    }
}
