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
@Schema(description = "Ticket provider information")
public class ProviderDTO {

    @Schema(description = "Provider ID", example = "1")
    private Long id;

    @Schema(description = "Provider name", example = "Ticketmaster")
    private String name;

    @Schema(description = "Display name", example = "Ticketmaster")
    private String displayName;

    @Schema(description = "Logo URL")
    private String logoUrl;

    @Schema(description = "Website URL", example = "https://www.ticketmaster.com")
    private String websiteUrl;

    @Schema(description = "Trust score (0-100)", example = "85")
    private Integer trustScore;

    @Schema(description = "Fee percentage", example = "18.00")
    private BigDecimal feePercentage;

    @Schema(description = "Has buyer protection", example = "true")
    private Boolean hasBuyerProtection;
}
