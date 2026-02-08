package com.worldcup.matchservice.controller;

import com.worldcup.matchservice.scheduler.DataSyncScheduler;
import com.worldcup.matchservice.service.MatchService;
import com.worldcup.matchservice.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin REST Controller
 * Administrative operations like manual data sync, cache management, etc.
 * Base path: /api/admin
 *
 * TODO: Add JWT authentication to secure these endpoints
 */
@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin", description = "Administrative operations (requires authentication)")
public class AdminController {

    private final MatchService matchService;
    private final TeamService teamService;
    private final Optional<DataSyncScheduler> dataSyncScheduler;

    public AdminController(MatchService matchService,
                          TeamService teamService,
                          @Autowired(required = false) DataSyncScheduler dataSyncScheduler) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.dataSyncScheduler = Optional.ofNullable(dataSyncScheduler);
    }

    /**
     * Manually trigger data synchronization
     */
    @PostMapping("/sync")
    @Operation(summary = "Trigger data sync", description = "Manually trigger data synchronization from external API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sync triggered successfully"),
            @ApiResponse(responseCode = "404", description = "Data sync not enabled"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> triggerDataSync() {
        log.info("POST /api/admin/sync - Manual data sync triggered");

        if (dataSyncScheduler.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Data sync is not enabled. Set data-sync.enabled=true to enable.");
            return ResponseEntity.status(404).body(response);
        }

        try {
            dataSyncScheduler.get().triggerManualSync();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data sync triggered successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering data sync", e);

            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger data sync: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get sync status
     */
    @GetMapping("/sync/status")
    @Operation(summary = "Get sync status", description = "Get current data synchronization status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getSyncStatus() {
        log.info("GET /api/admin/sync/status - Get sync status");

        Map<String, Object> status = new HashMap<>();
        status.put("syncEnabled", dataSyncScheduler.isPresent());
        status.put("scheduledTime", "3:00 AM daily");
        status.put("teamCount", teamService.countTeams());
        status.put("matchCount", matchService.countMatches());
        status.put("scheduledMatchCount", matchService.countMatchesByStatus("SCHEDULED"));
        status.put("liveMatchCount", matchService.countMatchesByStatus("LIVE"));
        status.put("finishedMatchCount", matchService.countMatchesByStatus("FINISHED"));

        return ResponseEntity.ok(status);
    }

    /**
     * Clear all caches
     */
    @PostMapping("/cache/clear")
    @Operation(summary = "Clear caches", description = "Clear all application caches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caches cleared successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> clearCaches() {
        log.info("POST /api/admin/cache/clear - Clear all caches");

        try {
            matchService.clearCache();
            teamService.clearCache();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "All caches cleared successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error clearing caches", e);

            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to clear caches: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get application health status
     */
    @GetMapping("/health")
    @Operation(summary = "Get health status", description = "Get application health and statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health status retrieved successfully")
    })
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        log.info("GET /api/admin/health - Get health status");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Match Service");
        health.put("version", "1.0.0");

        // Database statistics
        Map<String, Object> database = new HashMap<>();
        database.put("teams", teamService.countTeams());
        database.put("matches", matchService.countMatches());
        health.put("database", database);

        // Match statistics
        Map<String, Object> matchStats = new HashMap<>();
        matchStats.put("scheduled", matchService.countMatchesByStatus("SCHEDULED"));
        matchStats.put("live", matchService.countMatchesByStatus("LIVE"));
        matchStats.put("finished", matchService.countMatchesByStatus("FINISHED"));
        health.put("matchStatistics", matchStats);

        return ResponseEntity.ok(health);
    }
}
