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
@Schema(description = "Trending match with analytics data")
public class TrendingMatchDTO {

    @Schema(description = "Match ID")
    private Long matchId;

    @Schema(description = "Trending rank (1 = most trending)")
    private int rank;

    @Schema(description = "Popularity score (0-100)")
    private int popularityScore;

    @Schema(description = "Best deal score for this match")
    private int bestDealScore;

    @Schema(description = "Lowest available price")
    private BigDecimal lowestPrice;

    @Schema(description = "Average price across providers")
    private BigDecimal averagePrice;

    @Schema(description = "Price range (highest - lowest)")
    private BigDecimal priceSpread;

    @Schema(description = "Number of providers with offers")
    private int numProviders;

    @Schema(description = "Overall price trend", allowableValues = {"UP", "DOWN", "STABLE"})
    private String priceTrend;

    @Schema(description = "Best provider name")
    private String bestProviderName;

    @Schema(description = "Maximum savings percentage")
    private BigDecimal maxSavingsPercentage;

    @Schema(description = "Recommendation to buy now or wait")
    private String bestTimeToBuy;

    @Schema(description = "Trending reason explanation")
    private String trendingReason;
}
