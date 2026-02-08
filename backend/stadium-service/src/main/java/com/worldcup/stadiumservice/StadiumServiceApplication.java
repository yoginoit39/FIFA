package com.worldcup.stadiumservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Application class for FIFA World Cup 2026 Stadium Service
 *
 * This service handles:
 * - Stadium information management (CRUD operations)
 * - Stadium location and capacity data
 * - Filtering by city, country, etc.
 * - Caching for performance optimization
 *
 * @author FIFA World Cup 2026 Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class StadiumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StadiumServiceApplication.class, args);
    }
}
