package com.worldcup.matchservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Match Data Transfer Object
 * Used for API responses (list view)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Match information for FIFA World Cup 2026")
public class MatchDTO {

    @Schema(description = "Match ID", example = "1")
    private Long id;

    @Schema(description = "External API identifier", example = "12345")
    private String externalApiId;

    @Schema(description = "Home team information", required = true)
    @NotNull(message = "Home team is required")
    private TeamDTO homeTeam;

    @Schema(description = "Away team information", required = true)
    @NotNull(message = "Away team is required")
    private TeamDTO awayTeam;

    @Schema(description = "Stadium ID", example = "1")
    private Long stadiumId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Match date", example = "2026-06-11", required = true)
    @NotNull(message = "Match date is required")
    private LocalDate matchDate;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Match time", example = "14:00")
    private LocalTime matchTime;

    @Schema(description = "Match status", example = "SCHEDULED",
            allowableValues = {"SCHEDULED", "LIVE", "FINISHED", "POSTPONED", "CANCELLED"})
    private String status;

    @Schema(description = "Home team score", example = "2")
    private Integer homeScore;

    @Schema(description = "Away team score", example = "1")
    private Integer awayScore;

    @Schema(description = "Tournament round", example = "Group Stage")
    private String round;

    @Schema(description = "Group name", example = "Group A")
    private String groupName;

    @Schema(description = "Venue name", example = "MetLife Stadium")
    private String venueName;

    @Schema(description = "Venue city", example = "East Rutherford")
    private String venueCity;

    @Schema(description = "Venue country", example = "USA")
    private String venueCountry;

    @Schema(description = "Record creation timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime updatedAt;
}
