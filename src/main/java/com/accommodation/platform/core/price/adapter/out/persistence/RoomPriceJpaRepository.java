package com.accommodation.platform.core.price.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accommodation.platform.core.price.domain.enums.PriceType;

public interface RoomPriceJpaRepository extends JpaRepository<RoomPriceJpaEntity, Long> {

    List<RoomPriceJpaEntity> findByRoomOptionIdAndDateBetweenOrderByDateAsc(
            Long roomOptionId, LocalDate startDate, LocalDate endDate);

    List<RoomPriceJpaEntity> findByRoomOptionIdAndPriceTypeAndDateBetweenOrderByDateAsc(
            Long roomOptionId, PriceType priceType, LocalDate startDate, LocalDate endDate);
}
