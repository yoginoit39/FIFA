package com.worldcup.ticketservice.controller;

import com.worldcup.ticketservice.dto.TicketLinkDTO;
import com.worldcup.ticketservice.service.TicketLinkService;
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
 * REST Controller for Ticket Link operations
 * Base path: /api/tickets
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ticket Links", description = "Ticket booking link management APIs")
public class TicketLinkController {

    private final TicketLinkService ticketLinkService;

    /**
     * Get all ticket links
     */
    @GetMapping
    @Operation(summary = "Get all ticket links", description = "Retrieve all ticket booking links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket links",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketLinkDTO.class)))
    })
    public ResponseEntity<List<TicketLinkDTO>> getAllTicketLinks() {
        log.info("GET /api/tickets - Get all ticket links");
        List<TicketLinkDTO> ticketLinks = ticketLinkService.getAllTicketLinks();
        return ResponseEntity.ok(ticketLinks);
    }

    /**
     * Get ticket link by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket link by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket link"),
            @ApiResponse(responseCode = "404", description = "Ticket link not found")
    })
    public ResponseEntity<TicketLinkDTO> getTicketLinkById(
            @Parameter(description = "Ticket link ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/tickets/{} - Get ticket link by ID", id);
        TicketLinkDTO ticketLink = ticketLinkService.getTicketLinkById(id);
        return ResponseEntity.ok(ticketLink);
    }

    /**
     * Get ticket links by match ID
     */
    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get ticket links for a match", description = "Retrieve all ticket booking links for a specific match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket links")
    })
    public ResponseEntity<List<TicketLinkDTO>> getTicketLinksByMatch(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long matchId) {
        log.info("GET /api/tickets/match/{} - Get ticket links by match", matchId);
        List<TicketLinkDTO> ticketLinks = ticketLinkService.getTicketLinksByMatch(matchId);
        return ResponseEntity.ok(ticketLinks);
    }

    /**
     * Get ticket links by provider
     */
    @GetMapping("/provider/{providerName}")
    @Operation(summary = "Get ticket links by provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket links")
    })
    public ResponseEntity<List<TicketLinkDTO>> getTicketLinksByProvider(
            @Parameter(description = "Provider name", required = true, example = "FIFA Official")
            @PathVariable String providerName) {
        log.info("GET /api/tickets/provider/{} - Get ticket links by provider", providerName);
        List<TicketLinkDTO> ticketLinks = ticketLinkService.getTicketLinksByProvider(providerName);
        return ResponseEntity.ok(ticketLinks);
    }

    /**
     * Get ticket links by availability status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get ticket links by availability status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket links")
    })
    public ResponseEntity<List<TicketLinkDTO>> getTicketLinksByStatus(
            @Parameter(description = "Availability status", required = true, example = "AVAILABLE")
            @PathVariable String status) {
        log.info("GET /api/tickets/status/{} - Get ticket links by status", status);
        List<TicketLinkDTO> ticketLinks = ticketLinkService.getTicketLinksByStatus(status);
        return ResponseEntity.ok(ticketLinks);
    }

    /**
     * Create new ticket link
     */
    @PostMapping
    @Operation(summary = "Create new ticket link", description = "Create a new ticket booking link (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket link created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<TicketLinkDTO> createTicketLink(
            @Valid @RequestBody TicketLinkDTO ticketLinkDTO) {
        log.info("POST /api/tickets - Create new ticket link");
        TicketLinkDTO createdTicketLink = ticketLinkService.createTicketLink(ticketLinkDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicketLink);
    }

    /**
     * Update existing ticket link
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update ticket link", description = "Update an existing ticket link (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket link updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ticket link not found")
    })
    public ResponseEntity<TicketLinkDTO> updateTicketLink(
            @Parameter(description = "Ticket link ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TicketLinkDTO ticketLinkDTO) {
        log.info("PUT /api/tickets/{} - Update ticket link", id);
        TicketLinkDTO updatedTicketLink = ticketLinkService.updateTicketLink(id, ticketLinkDTO);
        return ResponseEntity.ok(updatedTicketLink);
    }

    /**
     * Delete ticket link
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ticket link", description = "Delete a ticket link (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket link deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ticket link not found")
    })
    public ResponseEntity<Void> deleteTicketLink(
            @Parameter(description = "Ticket link ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/tickets/{} - Delete ticket link", id);
        ticketLinkService.deleteTicketLink(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all ticket links for a match
     */
    @DeleteMapping("/match/{matchId}")
    @Operation(summary = "Delete all ticket links for a match", description = "Delete all ticket links for a specific match (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket links deleted successfully")
    })
    public ResponseEntity<Void> deleteTicketLinksByMatch(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long matchId) {
        log.info("DELETE /api/tickets/match/{} - Delete all ticket links for match", matchId);
        ticketLinkService.deleteTicketLinksByMatch(matchId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get ticket link count
     */
    @GetMapping("/count")
    @Operation(summary = "Count ticket links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    })
    public ResponseEntity<Long> countTicketLinks() {
        log.info("GET /api/tickets/count - Count ticket links");
        long count = ticketLinkService.countTicketLinks();
        return ResponseEntity.ok(count);
    }

    /**
     * Get ticket link count by match
     */
    @GetMapping("/count/match/{matchId}")
    @Operation(summary = "Count ticket links by match")
    public ResponseEntity<Long> countTicketLinksByMatch(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long matchId) {
        log.info("GET /api/tickets/count/match/{} - Count ticket links by match", matchId);
        long count = ticketLinkService.countTicketLinksByMatch(matchId);
        return ResponseEntity.ok(count);
    }
}
