package com.worldcup.dealfinderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Price snapshot from a ticket provider")
public class PriceSnapshotDTO {

    @Schema(description = "Snapshot ID")
    private Long id;

    @Schema(description = "Match ID")
    private Long matchId;

    @Schema(description = "Provider name", example = "Ticketmaster")
    private String providerName;

    @Schema(description = "Provider logo URL")
    private String providerLogoUrl;

    @Schema(description = "Ticket category", example = "GENERAL")
    private String category;

    @Schema(description = "Base price before fees", example = "150.00")
    private BigDecimal basePrice;

    @Schema(description = "Fee amount", example = "27.00")
    private BigDecimal feeAmount;

    @Schema(description = "Total price including fees", example = "177.00")
    private BigDecimal totalPrice;

    @Schema(description = "Currency", example = "USD")
    private String currency;

    @Schema(description = "Availability status", example = "AVAILABLE")
    private String availabilityStatus;

    @Schema(description = "Booking URL")
    private String bookingUrl;

    @Schema(description = "When this price was fetched")
    private LocalDateTime fetchedAt;
}
