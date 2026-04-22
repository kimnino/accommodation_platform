package com.accommodation.platform;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.accommodation.platform.common.config.TestSecurityConfig;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public abstract class IntegrationTestBase {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);
}
