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
@Schema(description = "Deal score for a provider's ticket offer")
public class DealScoreDTO {

    @Schema(description = "Deal score ID")
    private Long id;

    @Schema(description = "Match ID")
    private Long matchId;

    @Schema(description = "Provider name", example = "SeatGeek")
    private String providerName;

    @Schema(description = "Provider logo URL")
    private String providerLogoUrl;

    @Schema(description = "Provider trust score (0-100)", example = "80")
    private Integer trustScore;

    @Schema(description = "Fee percentage", example = "12.00")
    private BigDecimal feePercentage;

    @Schema(description = "Has buyer protection", example = "true")
    private Boolean hasBuyerProtection;

    @Schema(description = "Ticket category", example = "GENERAL")
    private String category;

    @Schema(description = "Deal score (0-100, higher is better)", example = "85")
    private Integer dealScore;

    @Schema(description = "Current price", example = "120.00")
    private BigDecimal currentPrice;

    @Schema(description = "Market average price", example = "180.00")
    private BigDecimal marketAverage;

    @Schema(description = "Savings percentage", example = "33.33")
    private BigDecimal savingsPercentage;

    @Schema(description = "Price trend", example = "DOWN", allowableValues = {"UP", "DOWN", "STABLE"})
    private String priceTrend;

    @Schema(description = "Trend percentage change", example = "-5.20")
    private BigDecimal trendPercentage;

    @Schema(description = "7-day price low", example = "110.00")
    private BigDecimal price7dLow;

    @Schema(description = "7-day price high", example = "200.00")
    private BigDecimal price7dHigh;

    @Schema(description = "Best time to buy recommendation", example = "NOW")
    private String bestTimeToBuy;

    @Schema(description = "Recommendation text")
    private String recommendation;

    @Schema(description = "Direct booking URL")
    private String bookingUrl;
}
