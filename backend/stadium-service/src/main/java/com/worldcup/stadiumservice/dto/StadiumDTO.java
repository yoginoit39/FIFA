package com.worldcup.stadiumservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stadium Data Transfer Object
 * Used for API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Stadium information for FIFA World Cup 2026")
public class StadiumDTO {

    @Schema(description = "Stadium ID", example = "1")
    private Long id;

    @NotBlank(message = "Stadium name is required")
    @Size(max = 200, message = "Stadium name must not exceed 200 characters")
    @Schema(description = "Stadium name", example = "MetLife Stadium", required = true)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City", example = "East Rutherford", required = true)
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    @Schema(description = "State/Province", example = "New Jersey")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Schema(description = "Country", example = "USA", required = true)
    private String country;

    @Schema(description = "Stadium capacity", example = "82500")
    private Integer capacity;

    @Schema(description = "Latitude coordinate", example = "40.8136110")
    private BigDecimal latitude;

    @Schema(description = "Longitude coordinate", example = "-74.0744440")
    private BigDecimal longitude;

    @Schema(description = "Full address", example = "1 MetLife Stadium Dr, East Rutherford, NJ 07073")
    private String address;

    @Schema(description = "Stadium image URL", example = "https://example.com/images/metlife.jpg")
    private String imageUrl;

    @Schema(description = "Stadium description", example = "Home of the New York Giants and Jets, hosting the World Cup Final")
    private String description;

    @Schema(description = "Record creation timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2026-02-06T10:30:00")
    private LocalDateTime updatedAt;
}
