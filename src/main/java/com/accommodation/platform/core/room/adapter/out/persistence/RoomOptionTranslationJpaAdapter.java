package com.accommodation.platform.core.room.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.core.room.application.port.out.PersistRoomOptionTranslationPort;

@Repository
@RequiredArgsConstructor
public class RoomOptionTranslationJpaAdapter implements PersistRoomOptionTranslationPort {

    private final RoomOptionTranslationJpaRepository jpaRepository;

    @Override
    public void saveAll(List<RoomOptionTranslationJpaEntity> translations) {

        jpaRepository.saveAll(translations);
    }

    @Override
    public void deleteByRoomOptionId(Long roomOptionId) {

        jpaRepository.deleteByRoomOptionId(roomOptionId);
    }
}
