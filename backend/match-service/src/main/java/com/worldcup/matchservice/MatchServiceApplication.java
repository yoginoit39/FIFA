package com.worldcup.matchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application class for FIFA World Cup 2026 Match Service
 *
 * This service handles:
 * - Match data management (CRUD operations)
 * - Team data management
 * - Integration with external FIFA data APIs
 * - Scheduled data synchronization
 * - Caching for performance optimization
 *
 * @author FIFA World Cup 2026 Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MatchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchServiceApplication.class, args);
    }
}
