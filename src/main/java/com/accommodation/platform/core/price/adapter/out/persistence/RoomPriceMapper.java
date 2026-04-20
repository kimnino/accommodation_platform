package com.accommodation.platform.core.price.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.accommodation.platform.core.price.domain.model.RoomPrice;

@Component
public class RoomPriceMapper {

    public RoomPrice toDomain(RoomPriceJpaEntity entity) {

        RoomPrice price = RoomPrice.builder()
                .id(entity.getId())
                .roomOptionId(entity.getRoomOptionId())
                .date(entity.getDate())
                .priceType(entity.getPriceType())
                .basePrice(entity.getBasePrice())
                .sellingPrice(entity.getSellingPrice())
                .taxIncluded(entity.isTaxIncluded())
                .build();

        price.setCreatedAt(entity.getCreatedAt());
        price.setUpdatedAt(entity.getUpdatedAt());

        return price;
    }

    public RoomPriceJpaEntity toJpaEntity(RoomPrice domain) {

        return new RoomPriceJpaEntity(
                domain.getId(),
                domain.getRoomOptionId(),
                domain.getDate(),
                domain.getPriceType(),
                domain.getBasePrice(),
                domain.getSellingPrice(),
                domain.isTaxIncluded());
    }
}
