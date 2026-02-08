package com.worldcup.ticketservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TicketLink Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ticket booking link information")
public class TicketLinkDTO {

    @Schema(description = "Ticket link ID", example = "1")
    private Long id;

    @NotNull(message = "Match ID is required")
    @Schema(description = "Match ID", example = "1", required = true)
    private Long matchId;

    @NotBlank(message = "Provider name is required")
    @Size(max = 100, message = "Provider name must not exceed 100 characters")
    @Schema(description = "Ticket provider name", example = "FIFA Official", required = true)
    private String providerName;

    @NotBlank(message = "Booking URL is required")
    @Schema(description = "Ticket booking URL", example = "https://www.fifa.com/tickets", required = true)
    private String bookingUrl;

    @Schema(description = "Price range", example = "$100 - $1000")
    private String priceRange;

    @Schema(description = "Minimum price for sorting", example = "100")
    private Integer minPrice;

    @Schema(description = "Availability status", example = "AVAILABLE",
            allowableValues = {"AVAILABLE", "SOLD_OUT", "NOT_YET_AVAILABLE"})
    private String availabilityStatus;

    @Schema(description = "Display priority (1 = highest)", example = "1")
    private Integer priority;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;
}
