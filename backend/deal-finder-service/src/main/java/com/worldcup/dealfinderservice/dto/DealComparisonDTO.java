package com.worldcup.dealfinderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Full deal comparison for a match across all providers")
public class DealComparisonDTO {

    @Schema(description = "Match ID")
    private Long matchId;

    @Schema(description = "Aggregated summary")
    private MatchDealSummaryDTO summary;

    @Schema(description = "Individual deals sorted by price")
    private List<DealScoreDTO> deals;

    @Schema(description = "Last time prices were updated")
    private LocalDateTime lastUpdated;
}
