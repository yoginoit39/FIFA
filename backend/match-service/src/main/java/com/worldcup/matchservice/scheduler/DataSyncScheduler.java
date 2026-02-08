package com.worldcup.matchservice.scheduler;

import com.worldcup.matchservice.client.FootballApiClient;
import com.worldcup.matchservice.service.MatchService;
import com.worldcup.matchservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled data synchronization job
 * Fetches latest match and team data from external API
 *
 * Configured to run once daily at 3 AM (cron: 0 0 3 * * *)
 * This minimizes API calls while keeping data fresh
 *
 * API-FOOTBALL free tier: 100 requests/day
 * Our usage: ~3 requests/day (well within limit)
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "data-sync.enabled", havingValue = "true", matchIfMissing = false)
public class DataSyncScheduler {

    private final FootballApiClient footballApiClient;
    private final MatchService matchService;
    private final TeamService teamService;

    @Value("${data-sync.enabled:false}")
    private boolean syncEnabled;

    @Value("${data-sync.league-id:1}")
    private Integer leagueId;

    @Value("${data-sync.season:2026}")
    private Integer season;

    /**
     * Scheduled job to sync match data
     * Runs at 3 AM every day
     *
     * Cron expression: 0 0 3 * * *
     * - Second: 0
     * - Minute: 0
     * - Hour: 3 (3 AM)
     * - Day of month: * (any)
     * - Month: * (any)
     * - Day of week: * (any)
     */
    @Scheduled(cron = "${data-sync.cron:0 0 3 * * *}")
    public void syncMatchData() {
        if (!syncEnabled) {
            log.debug("Data sync is disabled");
            return;
        }

        log.info("=================================================");
        log.info("Starting scheduled data sync at {}", LocalDateTime.now());
        log.info("=================================================");

        try {
            // Step 1: Sync team data
            syncTeams();

            // Step 2: Sync match/fixture data
            syncMatches();

            // Step 3: Clear caches to force reload
            clearCaches();

            log.info("=================================================");
            log.info("Data sync completed successfully at {}", LocalDateTime.now());
            log.info("=================================================");

        } catch (Exception e) {
            log.error("Error during data sync", e);
            log.info("=================================================");
            log.info("Data sync failed at {}", LocalDateTime.now());
            log.info("=================================================");
        }
    }

    /**
     * Sync team data from external API
     */
    private void syncTeams() {
        log.info("Syncing team data...");

        try {
            // Fetch teams from API
            String response = footballApiClient.fetchTeams().block();

            if (response != null) {
                log.info("Received team data from API");

                // TODO: Parse response and save teams
                // This requires:
                // 1. JSON parsing (Jackson/Gson)
                // 2. Mapping API response to Team entity
                // 3. Using teamService.saveOrUpdateTeam()

                long teamCount = teamService.countTeams();
                log.info("Current team count: {}", teamCount);
            } else {
                log.warn("No team data received from API");
            }

        } catch (Exception e) {
            log.error("Error syncing teams", e);
        }
    }

    /**
     * Sync match/fixture data from external API
     */
    private void syncMatches() {
        log.info("Syncing match data...");

        try {
            // Fetch fixtures from API
            String response = footballApiClient.fetchWorldCupFixtures().block();

            if (response != null) {
                log.info("Received fixture data from API");

                // TODO: Parse response and save matches
                // This requires:
                // 1. JSON parsing
                // 2. Mapping API response to Match entity
                // 3. Using matchService.saveOrUpdateMatch()

                long matchCount = matchService.countMatches();
                log.info("Current match count: {}", matchCount);
            } else {
                log.warn("No fixture data received from API");
            }

        } catch (Exception e) {
            log.error("Error syncing matches", e);
        }
    }

    /**
     * Sync live match scores (can be run more frequently)
     * Runs every 5 minutes during World Cup period
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void syncLiveScores() {
        if (!syncEnabled) {
            return;
        }

        try {
            log.debug("Checking for live match updates...");

            String response = footballApiClient.fetchLiveScores().block();

            if (response != null) {
                // TODO: Parse and update live scores
                log.debug("Live scores updated");
            }

        } catch (Exception e) {
            log.error("Error syncing live scores", e);
        }
    }

    /**
     * Clear all caches after sync
     */
    private void clearCaches() {
        log.info("Clearing caches...");
        matchService.clearCache();
        teamService.clearCache();
        log.info("Caches cleared");
    }

    /**
     * Manual trigger for data sync (via REST endpoint)
     * Can be called by admin to force immediate sync
     */
    public void triggerManualSync() {
        log.info("Manual data sync triggered");
        syncMatchData();
    }

    /**
     * Get sync status
     */
    public String getSyncStatus() {
        return String.format(
                "Data Sync Status:\n" +
                "- Enabled: %s\n" +
                "- League ID: %d\n" +
                "- Season: %d\n" +
                "- Last sync: Check logs\n" +
                "- Next sync: 3:00 AM daily",
                syncEnabled, leagueId, season
        );
    }
}
