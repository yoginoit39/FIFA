package com.worldcup.stadiumservice.controller;

import com.worldcup.stadiumservice.dto.StadiumDTO;
import com.worldcup.stadiumservice.service.StadiumService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Stadium operations
 * Base path: /api/stadiums
 */
@RestController
@RequestMapping("/api/stadiums")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stadiums", description = "Stadium management APIs for FIFA World Cup 2026")
public class StadiumController {

    private final StadiumService stadiumService;

    /**
     * Get all stadiums with pagination
     */
    @GetMapping
    @Operation(summary = "Get all stadiums", description = "Retrieve all stadiums with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadiums",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<StadiumDTO>> getAllStadiums(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/stadiums - Get all stadiums (page: {}, size: {})", page, size);
        Page<StadiumDTO> stadiums = stadiumService.getAllStadiums(page, size);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * Get all stadiums (no pagination)
     */
    @GetMapping("/all")
    @Operation(summary = "Get all stadiums (no pagination)", description = "Retrieve all stadiums without pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadiums",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StadiumDTO>> getAllStadiumsList() {
        log.info("GET /api/stadiums/all - Get all stadiums (no pagination)");
        List<StadiumDTO> stadiums = stadiumService.getAllStadiums();
        return ResponseEntity.ok(stadiums);
    }

    /**
     * Get stadium by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get stadium by ID", description = "Retrieve a specific stadium by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadium",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "404", description = "Stadium not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StadiumDTO> getStadiumById(
            @Parameter(description = "Stadium ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/stadiums/{} - Get stadium by ID", id);
        StadiumDTO stadium = stadiumService.getStadiumById(id);
        return ResponseEntity.ok(stadium);
    }

    /**
     * Get stadium by name
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "Get stadium by name", description = "Retrieve a stadium by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadium",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "404", description = "Stadium not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StadiumDTO> getStadiumByName(
            @Parameter(description = "Stadium name", required = true, example = "MetLife Stadium")
            @PathVariable String name) {
        log.info("GET /api/stadiums/name/{} - Get stadium by name", name);
        StadiumDTO stadium = stadiumService.getStadiumByName(name);
        return ResponseEntity.ok(stadium);
    }

    /**
     * Get stadiums by city
     */
    @GetMapping("/by-city/{city}")
    @Operation(summary = "Get stadiums by city", description = "Retrieve all stadiums in a specific city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadiums",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StadiumDTO>> getStadiumsByCity(
            @Parameter(description = "City name", required = true, example = "East Rutherford")
            @PathVariable String city) {
        log.info("GET /api/stadiums/by-city/{} - Get stadiums by city", city);
        List<StadiumDTO> stadiums = stadiumService.getStadiumsByCity(city);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * Get stadiums by country
     */
    @GetMapping("/by-country/{country}")
    @Operation(summary = "Get stadiums by country", description = "Retrieve all stadiums in a specific country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadiums",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StadiumDTO>> getStadiumsByCountry(
            @Parameter(description = "Country name", required = true, example = "USA")
            @PathVariable String country) {
        log.info("GET /api/stadiums/by-country/{} - Get stadiums by country", country);
        List<StadiumDTO> stadiums = stadiumService.getStadiumsByCountry(country);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * Get stadiums by state
     */
    @GetMapping("/by-state/{state}")
    @Operation(summary = "Get stadiums by state", description = "Retrieve all stadiums in a specific state/province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadiums",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StadiumDTO>> getStadiumsByState(
            @Parameter(description = "State/Province name", required = true, example = "Texas")
            @PathVariable String state) {
        log.info("GET /api/stadiums/by-state/{} - Get stadiums by state", state);
        List<StadiumDTO> stadiums = stadiumService.getStadiumsByState(state);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * Get stadiums ordered by capacity
     */
    @GetMapping("/by-capacity")
    @Operation(summary = "Get stadiums by capacity", description = "Retrieve all stadiums ordered by capacity (largest first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stadiums",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StadiumDTO>> getStadiumsByCapacity() {
        log.info("GET /api/stadiums/by-capacity - Get stadiums by capacity");
        List<StadiumDTO> stadiums = stadiumService.getStadiumsByCapacity();
        return ResponseEntity.ok(stadiums);
    }

    /**
     * Create new stadium
     */
    @PostMapping
    @Operation(summary = "Create new stadium", description = "Create a new stadium (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stadium created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StadiumDTO> createStadium(
            @Valid @RequestBody StadiumDTO stadiumDTO) {
        log.info("POST /api/stadiums - Create new stadium: {}", stadiumDTO.getName());
        StadiumDTO createdStadium = stadiumService.createStadium(stadiumDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStadium);
    }

    /**
     * Update existing stadium
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update stadium", description = "Update an existing stadium (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stadium updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StadiumDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Stadium not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StadiumDTO> updateStadium(
            @Parameter(description = "Stadium ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StadiumDTO stadiumDTO) {
        log.info("PUT /api/stadiums/{} - Update stadium", id);
        StadiumDTO updatedStadium = stadiumService.updateStadium(id, stadiumDTO);
        return ResponseEntity.ok(updatedStadium);
    }

    /**
     * Delete stadium
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stadium", description = "Delete a stadium (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stadium deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Stadium not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteStadium(
            @Parameter(description = "Stadium ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/stadiums/{} - Delete stadium", id);
        stadiumService.deleteStadium(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get stadium count
     */
    @GetMapping("/count")
    @Operation(summary = "Count stadiums", description = "Get total number of stadiums")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved count"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> countStadiums() {
        log.info("GET /api/stadiums/count - Count stadiums");
        long count = stadiumService.countStadiums();
        return ResponseEntity.ok(count);
    }

    /**
     * Get stadium count by country
     */
    @GetMapping("/count-by-country/{country}")
    @Operation(summary = "Count stadiums by country", description = "Get number of stadiums in a country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved count"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> countStadiumsByCountry(
            @Parameter(description = "Country name", required = true, example = "USA")
            @PathVariable String country) {
        log.info("GET /api/stadiums/count-by-country/{} - Count stadiums by country", country);
        long count = stadiumService.countStadiumsByCountry(country);
        return ResponseEntity.ok(count);
    }
}
