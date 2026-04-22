package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.room.application.port.out.LoadRoomTranslationPort;

@Repository
@RequiredArgsConstructor
public class RoomTranslationJpaAdapter implements LoadRoomTranslationPort {

    private final RoomTranslationJpaRepository jpaRepository;

    @Override
    public Optional<RoomTranslationJpaEntity> findByRoomIdAndLocale(Long roomId, String locale) {

        return jpaRepository.findByRoomIdAndLocale(roomId, locale);
    }

    @Override
    public List<RoomTranslationJpaEntity> findByRoomIdInAndLocale(List<Long> roomIds, String locale) {

        return jpaRepository.findByRoomIdInAndLocale(roomIds, locale);
    }
}
