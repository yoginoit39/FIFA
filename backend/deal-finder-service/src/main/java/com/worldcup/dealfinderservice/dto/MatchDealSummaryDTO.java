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
@Schema(description = "Aggregated deal summary for a match")
public class MatchDealSummaryDTO {

    @Schema(description = "Match ID")
    private Long matchId;

    @Schema(description = "Ticket category", example = "GENERAL")
    private String category;

    @Schema(description = "Lowest price across all providers", example = "85.00")
    private BigDecimal lowestPrice;

    @Schema(description = "Highest price across all providers", example = "350.00")
    private BigDecimal highestPrice;

    @Schema(description = "Average price across all providers", example = "180.00")
    private BigDecimal averagePrice;

    @Schema(description = "Name of the best-priced provider", example = "SeatGeek")
    private String bestProviderName;

    @Schema(description = "Best deal score", example = "92")
    private Integer bestDealScore;

    @Schema(description = "Number of providers with pricing", example = "4")
    private Integer numProviders;

    @Schema(description = "Overall price trend", example = "DOWN")
    private String overallTrend;

    @Schema(description = "Best time to buy", example = "NOW")
    private String bestTimeToBuy;
}
