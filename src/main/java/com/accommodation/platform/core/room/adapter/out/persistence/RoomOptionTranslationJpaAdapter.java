package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.room.application.port.out.LoadRoomOptionTranslationPort;
import com.accommodation.platform.core.room.application.port.out.PersistRoomOptionTranslationPort;

@Repository
@RequiredArgsConstructor
public class RoomOptionTranslationJpaAdapter implements PersistRoomOptionTranslationPort, LoadRoomOptionTranslationPort {

    private final RoomOptionTranslationJpaRepository jpaRepository;

    @Override
    public void saveAll(List<RoomOptionTranslationJpaEntity> translations) {

        jpaRepository.saveAll(translations);
    }

    @Override
    public void deleteByRoomOptionId(Long roomOptionId) {

        jpaRepository.deleteByRoomOptionId(roomOptionId);
    }

    @Override
    public Optional<RoomOptionTranslationJpaEntity> findByRoomOptionIdAndLocale(Long roomOptionId, String locale) {

        return jpaRepository.findByRoomOptionIdAndLocale(roomOptionId, locale);
    }

    @Override
    public List<RoomOptionTranslationJpaEntity> findByRoomOptionIdInAndLocale(List<Long> roomOptionIds, String locale) {

        return jpaRepository.findByRoomOptionIdInAndLocale(roomOptionIds, locale);
    }
}
