package com.worldcup.dealfinderservice.controller;

import com.worldcup.dealfinderservice.dto.*;
import com.worldcup.dealfinderservice.service.DealComparisonService;
import com.worldcup.dealfinderservice.service.DealScoringService;
import com.worldcup.dealfinderservice.service.PriceFetchService;
import com.worldcup.dealfinderservice.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deals", description = "Ticket deal finding and price comparison APIs")
public class DealController {

    private final DealComparisonService dealComparisonService;
    private final ProviderService providerService;
    private final PriceFetchService priceFetchService;
    private final DealScoringService dealScoringService;

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get deal comparison for a match", description = "Returns all deals, summary, and last updated time for a specific match")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deal comparison retrieved successfully")
    })
    public ResponseEntity<DealComparisonDTO> getDealsForMatch(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long matchId) {
        log.info("GET /api/deals/match/{} - Get deals for match", matchId);
        DealComparisonDTO comparison = dealComparisonService.getDealsForMatch(matchId);
        return ResponseEntity.ok(comparison);
    }

    @GetMapping("/match/{matchId}/cheapest")
    @Operation(summary = "Get cheapest deal for a match", description = "Returns the single cheapest deal available for a match")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cheapest deal retrieved"),
        @ApiResponse(responseCode = "204", description = "No deals available")
    })
    public ResponseEntity<DealScoreDTO> getCheapestDeal(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long matchId) {
        log.info("GET /api/deals/match/{}/cheapest - Get cheapest deal", matchId);
        DealScoreDTO cheapest = dealComparisonService.getCheapestDeal(matchId);
        if (cheapest == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cheapest);
    }

    @GetMapping("/top")
    @Operation(summary = "Get top deals", description = "Returns the best deals across all matches, sorted by deal score")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Top deals retrieved successfully")
    })
    public ResponseEntity<List<DealScoreDTO>> getTopDeals(
            @Parameter(description = "Maximum number of deals to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/deals/top - Get top {} deals", limit);
        List<DealScoreDTO> topDeals = dealComparisonService.getTopDeals(limit);
        return ResponseEntity.ok(topDeals);
    }

    @GetMapping("/match/{matchId}/history")
    @Operation(summary = "Get price history for a match", description = "Returns 7-day price history snapshots for a match")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Price history retrieved successfully")
    })
    public ResponseEntity<List<PriceSnapshotDTO>> getPriceHistory(
            @Parameter(description = "Match ID", required = true, example = "1")
            @PathVariable Long matchId) {
        log.info("GET /api/deals/match/{}/history - Get price history", matchId);
        List<PriceSnapshotDTO> history = dealComparisonService.getPriceHistory(matchId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/summaries")
    @Operation(summary = "Get all match deal summaries", description = "Returns deal summaries for all matches, sorted by lowest price")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Summaries retrieved successfully")
    })
    public ResponseEntity<List<MatchDealSummaryDTO>> getAllSummaries() {
        log.info("GET /api/deals/summaries - Get all match deal summaries");
        List<MatchDealSummaryDTO> summaries = dealComparisonService.getAllMatchSummaries();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/providers")
    @Operation(summary = "Get all active providers", description = "Returns all active ticket providers ordered by priority")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Providers retrieved successfully")
    })
    public ResponseEntity<List<ProviderDTO>> getProviders() {
        log.info("GET /api/deals/providers - Get all active providers");
        List<ProviderDTO> providers = providerService.getAllActiveProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/providers/{id}")
    @Operation(summary = "Get provider by ID", description = "Returns a specific ticket provider by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found")
    })
    public ResponseEntity<ProviderDTO> getProviderById(
            @Parameter(description = "Provider ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/deals/providers/{} - Get provider by ID", id);
        ProviderDTO provider = providerService.getProviderById(id);
        return ResponseEntity.ok(provider);
    }

    @PostMapping("/admin/fetch-prices")
    @Operation(summary = "Trigger price fetch", description = "Manually triggers a price fetch from all providers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Price fetch completed")
    })
    public ResponseEntity<Map<String, Object>> fetchPrices() {
        log.info("POST /api/deals/admin/fetch-prices - Triggering manual price fetch");
        int recordsFetched = priceFetchService.fetchAllPrices();
        return ResponseEntity.ok(Map.of(
                "status", "completed",
                "recordsFetched", recordsFetched
        ));
    }

    @PostMapping("/admin/compute-scores")
    @Operation(summary = "Trigger score computation", description = "Manually triggers deal score computation for all matches")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Score computation completed")
    })
    public ResponseEntity<Map<String, Object>> computeScores() {
        log.info("POST /api/deals/admin/compute-scores - Triggering manual score computation");
        dealScoringService.computeAllScores();
        return ResponseEntity.ok(Map.of("status", "completed"));
    }
}
