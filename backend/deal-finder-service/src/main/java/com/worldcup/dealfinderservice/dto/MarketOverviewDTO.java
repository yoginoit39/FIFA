package com.worldcup.dealfinderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Overall market overview and statistics")
public class MarketOverviewDTO {

    @Schema(description = "Total matches tracked")
    private int totalMatches;

    @Schema(description = "Total active providers")
    private int totalProviders;

    @Schema(description = "Total deal scores computed")
    private int totalDeals;

    @Schema(description = "Overall lowest price across all matches")
    private BigDecimal overallLowestPrice;

    @Schema(description = "Overall highest price across all matches")
    private BigDecimal overallHighestPrice;

    @Schema(description = "Average ticket price across all matches")
    private BigDecimal averagePrice;

    @Schema(description = "Average deal score across all deals")
    private double averageDealScore;

    @Schema(description = "Number of deals with score >= 70 (hot deals)")
    private int hotDealCount;

    @Schema(description = "Number of matches with prices trending down")
    private int pricesDownCount;

    @Schema(description = "Number of matches with prices trending up")
    private int pricesUpCount;

    @Schema(description = "Number of matches with stable prices")
    private int pricesStableCount;

    @Schema(description = "Percentage of matches recommended to buy now")
    private double buyNowPercentage;
}
