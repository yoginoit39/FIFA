package com.worldcup.matchservice.controller;

import com.worldcup.matchservice.dto.TeamDTO;
import com.worldcup.matchservice.service.TeamService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Team operations
 * Base path: /api/teams
 */
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teams", description = "Team management APIs for FIFA World Cup 2026")
public class TeamController {

    private final TeamService teamService;

    /**
     * Get all teams
     */
    @GetMapping
    @Operation(summary = "Get all teams", description = "Retrieve all teams ordered by FIFA ranking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved teams",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        log.info("GET /api/teams - Get all teams");
        List<TeamDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    /**
     * Get team by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", description = "Retrieve a specific team by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamDTO.class))),
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeamDTO> getTeamById(
            @Parameter(description = "Team ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/teams/{} - Get team by ID", id);
        TeamDTO team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    /**
     * Get team by country
     */
    @GetMapping("/country/{country}")
    @Operation(summary = "Get team by country", description = "Retrieve a team by country name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamDTO.class))),
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeamDTO> getTeamByCountry(
            @Parameter(description = "Country name", required = true, example = "USA")
            @PathVariable String country) {
        log.info("GET /api/teams/country/{} - Get team by country", country);
        TeamDTO team = teamService.getTeamByCountry(country);
        return ResponseEntity.ok(team);
    }

    /**
     * Create new team
     */
    @PostMapping
    @Operation(summary = "Create new team", description = "Create a new team (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Team created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeamDTO> createTeam(
            @Valid @RequestBody TeamDTO teamDTO) {
        log.info("POST /api/teams - Create new team: {}", teamDTO.getName());
        TeamDTO createdTeam = teamService.createTeam(teamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    /**
     * Update existing team
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update team", description = "Update an existing team (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeamDTO> updateTeam(
            @Parameter(description = "Team ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO teamDTO) {
        log.info("PUT /api/teams/{} - Update team", id);
        TeamDTO updatedTeam = teamService.updateTeam(id, teamDTO);
        return ResponseEntity.ok(updatedTeam);
    }

    /**
     * Delete team
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team", description = "Delete a team (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Team deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "Team ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/teams/{} - Delete team", id);
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get team count
     */
    @GetMapping("/count")
    @Operation(summary = "Count teams", description = "Get total number of teams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved count"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> countTeams() {
        log.info("GET /api/teams/count - Count teams");
        long count = teamService.countTeams();
        return ResponseEntity.ok(count);
    }
}
