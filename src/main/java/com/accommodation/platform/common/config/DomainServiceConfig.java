package com.accommodation.platform.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.accommodation.platform.core.inventory.domain.service.InventoryDomainService;
import com.accommodation.platform.core.price.domain.service.PriceDomainService;

@Configuration
public class DomainServiceConfig {

    @Bean
    public InventoryDomainService inventoryDomainService() {
        return new InventoryDomainService();
    }

    @Bean
    public PriceDomainService priceDomainService() {
        return new PriceDomainService();
    }
}
