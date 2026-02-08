package com.worldcup.matchservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Team Data Transfer Object
 * Used for API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Team information for FIFA World Cup 2026")
public class TeamDTO {

    @Schema(description = "Team ID", example = "1")
    private Long id;

    @Schema(description = "External API identifier", example = "USA")
    private String externalApiId;

    @NotBlank(message = "Team name is required")
    @Size(max = 100, message = "Team name must not exceed 100 characters")
    @Schema(description = "Team name", example = "United States", required = true)
    private String name;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Schema(description = "Country", example = "USA", required = true)
    private String country;

    @Schema(description = "Team logo URL", example = "https://example.com/logos/usa.png")
    private String logoUrl;

    @Schema(description = "FIFA World Ranking", example = "13")
    private Integer fifaRanking;

    @Schema(description = "Record creation timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime updatedAt;
}
