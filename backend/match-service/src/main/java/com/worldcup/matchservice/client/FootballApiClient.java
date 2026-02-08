package com.worldcup.matchservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client for interacting with external football data APIs
 * Currently supports API-FOOTBALL via RapidAPI
 *
 * NOTE: This is a simplified version. Full implementation will include:
 * - Complete DTO models for API responses
 * - Proper error handling and retries
 * - Response parsing and mapping to internal entities
 * - Rate limiting logic
 * - Fallback to TheSportsDB
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FootballApiClient {

    @Qualifier("footballApiWebClient")
    private final WebClient footballApiWebClient;

    @Qualifier("sportsDbWebClient")
    private final WebClient sportsDbWebClient;

    /**
     * Fetch World Cup 2026 fixtures from API
     * Endpoint: /fixtures
     * Query params: league=1 (FIFA World Cup), season=2026
     *
     * This is a placeholder method. Full implementation will:
     * 1. Call the external API
     * 2. Parse the JSON response
     * 3. Map to internal Match and Team entities
     * 4. Return structured data
     */
    public Mono<String> fetchWorldCupFixtures() {
        log.info("Fetching World Cup 2026 fixtures from API-FOOTBALL");

        return footballApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("league", "1")  // FIFA World Cup
                        .queryParam("season", "2026")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("Successfully fetched fixtures"))
                .doOnError(error -> log.error("Error fetching fixtures: {}", error.getMessage()));
    }

    /**
     * Fetch team information from API
     * Endpoint: /teams
     *
     * Placeholder for team data sync
     */
    public Mono<String> fetchTeams() {
        log.info("Fetching team data from API-FOOTBALL");

        return footballApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/teams")
                        .queryParam("league", "1")
                        .queryParam("season", "2026")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("Successfully fetched teams"))
                .doOnError(error -> log.error("Error fetching teams: {}", error.getMessage()));
    }

    /**
     * Fetch live match scores
     * Endpoint: /fixtures
     * Query params: live=all
     *
     * Used to update scores for live matches
     */
    public Mono<String> fetchLiveScores() {
        log.info("Fetching live scores from API-FOOTBALL");

        return footballApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("live", "all")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("Successfully fetched live scores"))
                .doOnError(error -> log.error("Error fetching live scores: {}", error.getMessage()));
    }

    /**
     * Fallback: Fetch data from TheSportsDB
     * Used when API-FOOTBALL quota is exceeded
     */
    public Mono<String> fetchFromSportsDb(String endpoint) {
        log.info("Fetching data from TheSportsDB (fallback): {}", endpoint);

        return sportsDbWebClient
                .get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("Successfully fetched from TheSportsDB"))
                .doOnError(error -> log.error("Error fetching from TheSportsDB: {}", error.getMessage()));
    }

    /**
     * Check API quota/rate limit status
     * Returns current usage information
     */
    public void checkApiQuota() {
        log.info("API-FOOTBALL free tier: 100 requests/day");
        log.info("Current implementation uses database caching to minimize API calls");
        log.info("Scheduled sync runs once daily at 3 AM");
    }
}
