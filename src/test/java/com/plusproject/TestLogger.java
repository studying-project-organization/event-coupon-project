package com.plusproject;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger {
    private static final Logger log = LoggerFactory.getLogger(TestLogger.class);

    @BeforeAll
    static void printEnv() {
        log.info("SPRING_DATASOURCE_USERNAME: {}", System.getenv("SPRING_DATASOURCE_USERNAME"));
        log.info("SPRING_DATASOURCE_PASSWORD: {}", System.getenv("SPRING_DATASOURCE_PASSWORD"));
        log.info("JWT_SECRET_KEY: {}", System.getenv("JWT_SECRET_KEY"));
        log.info("Active Profile: {}", System.getProperty("spring.profiles.active"));
    }
}

