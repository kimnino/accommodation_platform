package com.accommodation.platform.core.price.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.accommodation.platform.core.price.application.port.out.LoadRoomPricePort;
import com.accommodation.platform.core.price.application.port.out.PersistRoomPricePort;
import com.accommodation.platform.core.price.domain.enums.PriceType;
import com.accommodation.platform.core.price.domain.model.RoomPrice;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoomPriceJpaAdapter implements PersistRoomPricePort, LoadRoomPricePort {

    private final RoomPriceJpaRepository jpaRepository;
    private final RoomPriceMapper mapper;

    @Override
    public RoomPrice save(RoomPrice roomPrice) {

        RoomPriceJpaEntity entity = mapper.toJpaEntity(roomPrice);
        RoomPriceJpaEntity saved = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<RoomPrice> saveAll(List<RoomPrice> roomPrices) {

        List<RoomPriceJpaEntity> entities = roomPrices.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<RoomPriceJpaEntity> savedEntities = jpaRepository.saveAllAndFlush(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<RoomPrice> findByRoomOptionIdAndDateRange(Long roomOptionId,
                                                          LocalDate startDate, LocalDate endDate) {

        return jpaRepository.findByRoomOptionIdAndDateBetweenOrderByDateAsc(roomOptionId, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<RoomPrice> findByRoomOptionIdAndPriceTypeAndDateRange(Long roomOptionId, PriceType priceType,
                                                                      LocalDate startDate, LocalDate endDate) {

        return jpaRepository.findByRoomOptionIdAndPriceTypeAndDateBetweenOrderByDateAsc(
                        roomOptionId, priceType, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByRoomOptionId(Long roomOptionId) {

        jpaRepository.deleteByRoomOptionId(roomOptionId);
    }
}
