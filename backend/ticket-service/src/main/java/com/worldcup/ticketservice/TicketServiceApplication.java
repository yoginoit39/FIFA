package com.worldcup.ticketservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Application class for FIFA World Cup 2026 Ticket Service
 *
 * This service handles:
 * - Ticket booking link management
 * - External ticket provider information
 * - Links to FIFA, Ticketmaster, StubHub, SeatGeek, etc.
 * - Price range and availability information
 *
 * @author FIFA World Cup 2026 Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class TicketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketServiceApplication.class, args);
    }
}
