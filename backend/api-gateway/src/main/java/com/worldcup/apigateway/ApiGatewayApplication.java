package com.worldcup.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application class for FIFA World Cup 2026 API Gateway
 *
 * This service handles:
 * - Request routing to microservices (Match, Stadium, Ticket)
 * - CORS configuration
 * - Request proxying with WebFlux
 *
 * @author FIFA World Cup 2026 Team
 * @version 1.0.0
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
