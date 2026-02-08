package com.worldcup.matchservice.controller;

import com.worldcup.matchservice.dto.MatchDTO;
import com.worldcup.matchservice.dto.MatchDetailDTO;
import com.worldcup.matchservice.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Match operations
 * Base path: /api/matches
 */
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Matches", description = "Match management APIs for FIFA World Cup 2026")
public class MatchController {

    private final MatchService matchService;

    /**
     * Get all matches with pagination
     */
    @GetMapping
    @Operation(summary = "Get all matches", description = "Retrieve all matches with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<MatchDTO>> getAllMatches(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/matches - Get all matches (page: {}, size: {})", page, size);
        Page<MatchDTO> matches = matchService.getAllMatches(page, size);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get match by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get match by ID", description = "Retrieve detailed information for a specific match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved match",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "Match not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MatchDetailDTO> getMatchById(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/matches/{} - Get match by ID", id);
        MatchDetailDTO match = matchService.getMatchById(id);
        return ResponseEntity.ok(match);
    }

    /**
     * Get upcoming matches
     */
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming matches", description = "Retrieve all upcoming scheduled matches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved upcoming matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getUpcomingMatches() {
        log.info("GET /api/matches/upcoming - Get upcoming matches");
        List<MatchDTO> matches = matchService.getUpcomingMatches();
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by date
     */
    @GetMapping("/by-date/{date}")
    @Operation(summary = "Get matches by date", description = "Retrieve all matches on a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByDate(
            @Parameter(description = "Match date (yyyy-MM-dd)", required = true, example = "2026-06-11")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/matches/by-date/{} - Get matches by date", date);
        List<MatchDTO> matches = matchService.getMatchesByDate(date);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by date range
     */
    @GetMapping("/by-date-range")
    @Operation(summary = "Get matches by date range", description = "Retrieve matches within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true, example = "2026-06-11")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true, example = "2026-07-19")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /api/matches/by-date-range - Get matches from {} to {}", startDate, endDate);
        List<MatchDTO> matches = matchService.getMatchesByDateRange(startDate, endDate);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by team
     */
    @GetMapping("/by-team/{teamId}")
    @Operation(summary = "Get matches by team", description = "Retrieve all matches for a specific team (home or away)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByTeam(
            @Parameter(description = "Team ID", required = true, example = "1")
            @PathVariable Long teamId) {
        log.info("GET /api/matches/by-team/{} - Get matches by team", teamId);
        List<MatchDTO> matches = matchService.getMatchesByTeam(teamId);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by stadium
     */
    @GetMapping("/by-stadium/{stadiumId}")
    @Operation(summary = "Get matches by stadium", description = "Retrieve all matches at a specific stadium")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByStadium(
            @Parameter(description = "Stadium ID", required = true, example = "1")
            @PathVariable Long stadiumId) {
        log.info("GET /api/matches/by-stadium/{} - Get matches by stadium", stadiumId);
        List<MatchDTO> matches = matchService.getMatchesByStadium(stadiumId);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by round
     */
    @GetMapping("/by-round/{round}")
    @Operation(summary = "Get matches by round", description = "Retrieve all matches in a specific tournament round")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByRound(
            @Parameter(description = "Round name", required = true, example = "Group Stage")
            @PathVariable String round) {
        log.info("GET /api/matches/by-round/{} - Get matches by round", round);
        List<MatchDTO> matches = matchService.getMatchesByRound(round);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by group
     */
    @GetMapping("/by-group/{groupName}")
    @Operation(summary = "Get matches by group", description = "Retrieve all matches in a specific group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByGroup(
            @Parameter(description = "Group name", required = true, example = "Group A")
            @PathVariable String groupName) {
        log.info("GET /api/matches/by-group/{} - Get matches by group", groupName);
        List<MatchDTO> matches = matchService.getMatchesByGroup(groupName);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get live matches
     */
    @GetMapping("/live")
    @Operation(summary = "Get live matches", description = "Retrieve all currently live matches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved live matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getLiveMatches() {
        log.info("GET /api/matches/live - Get live matches");
        List<MatchDTO> matches = matchService.getLiveMatches();
        return ResponseEntity.ok(matches);
    }

    /**
     * Get matches by status
     */
    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get matches by status", description = "Retrieve matches by status (SCHEDULED, LIVE, FINISHED, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matches",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MatchDTO>> getMatchesByStatus(
            @Parameter(description = "Match status", required = true, example = "SCHEDULED")
            @PathVariable String status) {
        log.info("GET /api/matches/by-status/{} - Get matches by status", status);
        List<MatchDTO> matches = matchService.getMatchesByStatus(status);
        return ResponseEntity.ok(matches);
    }

    /**
     * Create new match (Admin only)
     */
    @PostMapping
    @Operation(summary = "Create new match", description = "Create a new match (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MatchDTO> createMatch(
            @Valid @RequestBody MatchDTO matchDTO) {
        log.info("POST /api/matches - Create new match");
        MatchDTO createdMatch = matchService.createMatch(matchDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMatch);
    }

    /**
     * Update match (Admin only)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update match", description = "Update an existing match (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Match not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MatchDTO> updateMatch(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MatchDTO matchDTO) {
        log.info("PUT /api/matches/{} - Update match", id);
        MatchDTO updatedMatch = matchService.updateMatch(id, matchDTO);
        return ResponseEntity.ok(updatedMatch);
    }

    /**
     * Update match score (Admin only)
     */
    @PatchMapping("/{id}/score")
    @Operation(summary = "Update match score", description = "Update the score for a match (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "404", description = "Match not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MatchDTO> updateMatchScore(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Home team score", required = true, example = "2")
            @RequestParam Integer homeScore,
            @Parameter(description = "Away team score", required = true, example = "1")
            @RequestParam Integer awayScore) {
        log.info("PATCH /api/matches/{}/score - Update score: {}:{}", id, homeScore, awayScore);
        MatchDTO updatedMatch = matchService.updateMatchScore(id, homeScore, awayScore);
        return ResponseEntity.ok(updatedMatch);
    }

    /**
     * Update match status (Admin only)
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update match status", description = "Update the status of a match (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "404", description = "Match not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MatchDTO> updateMatchStatus(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New status", required = true, example = "LIVE")
            @RequestParam String status) {
        log.info("PATCH /api/matches/{}/status - Update status to: {}", id, status);
        MatchDTO updatedMatch = matchService.updateMatchStatus(id, status);
        return ResponseEntity.ok(updatedMatch);
    }

    /**
     * Delete match (Admin only)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete match", description = "Delete a match (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Match not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/matches/{} - Delete match", id);
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get match count
     */
    @GetMapping("/count")
    @Operation(summary = "Count matches", description = "Get total number of matches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved count"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> countMatches() {
        log.info("GET /api/matches/count - Count matches");
        long count = matchService.countMatches();
        return ResponseEntity.ok(count);
    }

    /**
     * Get match count by status
     */
    @GetMapping("/count-by-status/{status}")
    @Operation(summary = "Count matches by status", description = "Get number of matches by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved count"),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> countMatchesByStatus(
            @Parameter(description = "Match status", required = true, example = "SCHEDULED")
            @PathVariable String status) {
        log.info("GET /api/matches/count-by-status/{} - Count matches by status", status);
        long count = matchService.countMatchesByStatus(status);
        return ResponseEntity.ok(count);
    }
}
