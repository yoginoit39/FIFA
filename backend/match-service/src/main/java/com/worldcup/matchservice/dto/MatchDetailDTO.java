package com.worldcup.matchservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Match Detail Data Transfer Object
 * Extended information for single match view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed match information for FIFA World Cup 2026")
public class MatchDetailDTO {

    @Schema(description = "Match ID", example = "1")
    private Long id;

    @Schema(description = "External API identifier", example = "12345")
    private String externalApiId;

    @Schema(description = "Home team detailed information")
    private TeamDTO homeTeam;

    @Schema(description = "Away team detailed information")
    private TeamDTO awayTeam;

    @Schema(description = "Stadium ID (reference to Stadium Service)", example = "1")
    private Long stadiumId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Match date", example = "2026-06-11")
    private LocalDate matchDate;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Match time (local time)", example = "14:00")
    private LocalTime matchTime;

    @Schema(description = "Match status", example = "SCHEDULED")
    private String status;

    @Schema(description = "Home team score", example = "2")
    private Integer homeScore;

    @Schema(description = "Away team score", example = "1")
    private Integer awayScore;

    @Schema(description = "Tournament round", example = "Group Stage")
    private String round;

    @Schema(description = "Group name (if applicable)", example = "Group A")
    private String groupName;

    @Schema(description = "Stadium/Venue name", example = "MetLife Stadium")
    private String venueName;

    @Schema(description = "Venue city", example = "East Rutherford")
    private String venueCity;

    @Schema(description = "Venue country", example = "USA")
    private String venueCountry;

    @Schema(description = "Match result summary", example = "USA 2 - 1 Mexico")
    private String resultSummary;

    @Schema(description = "Is the match live?", example = "false")
    private Boolean isLive;

    @Schema(description = "Is the match upcoming (scheduled and in future)?", example = "true")
    private Boolean isUpcoming;

    @Schema(description = "Record creation timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * Build result summary from scores
     */
    public void buildResultSummary() {
        if (homeTeam != null && awayTeam != null) {
            this.resultSummary = String.format("%s %d - %d %s",
                    homeTeam.getName(),
                    homeScore != null ? homeScore : 0,
                    awayScore != null ? awayScore : 0,
                    awayTeam.getName());
        }
    }
}
