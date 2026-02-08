package com.worldcup.matchservice.mapper;

import com.worldcup.matchservice.dto.MatchDTO;
import com.worldcup.matchservice.dto.MatchDetailDTO;
import com.worldcup.matchservice.dto.TeamDTO;
import com.worldcup.matchservice.entity.Match;
import com.worldcup.matchservice.entity.Team;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

/**
 * MapStruct mapper for converting between entities and DTOs
 * MapStruct will generate the implementation at compile time
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MatchMapper {

    // ========================================================================
    // Team Mappings
    // ========================================================================

    /**
     * Convert Team entity to TeamDTO
     */
    TeamDTO toTeamDTO(Team team);

    /**
     * Convert TeamDTO to Team entity
     */
    Team toTeamEntity(TeamDTO teamDTO);

    /**
     * Convert list of Team entities to list of TeamDTOs
     */
    List<TeamDTO> toTeamDTOList(List<Team> teams);

    // ========================================================================
    // Match Mappings
    // ========================================================================

    /**
     * Convert Match entity to MatchDTO
     * Maps the status enum to string
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    MatchDTO toMatchDTO(Match match);

    /**
     * Convert MatchDTO to Match entity
     * Maps the status string to enum
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    Match toMatchEntity(MatchDTO matchDTO);

    /**
     * Convert list of Match entities to list of MatchDTOs
     */
    List<MatchDTO> toMatchDTOList(List<Match> matches);

    // ========================================================================
    // Match Detail Mappings
    // ========================================================================

    /**
     * Convert Match entity to MatchDetailDTO with additional computed fields
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "isLive", expression = "java(isMatchLive(match))")
    @Mapping(target = "isUpcoming", expression = "java(isMatchUpcoming(match))")
    @Mapping(target = "resultSummary", ignore = true)
    MatchDetailDTO toMatchDetailDTO(Match match);

    /**
     * Post-process MatchDetailDTO after mapping
     */
    @AfterMapping
    default void afterMappingDetail(@MappingTarget MatchDetailDTO dto) {
        dto.buildResultSummary();
    }

    /**
     * Convert list of Match entities to list of MatchDetailDTOs
     */
    List<MatchDetailDTO> toMatchDetailDTOList(List<Match> matches);

    // ========================================================================
    // Custom Mapping Methods
    // ========================================================================

    /**
     * Convert Match.MatchStatus enum to String
     */
    @Named("statusToString")
    default String statusToString(Match.MatchStatus status) {
        return status != null ? status.name() : null;
    }

    /**
     * Convert String to Match.MatchStatus enum
     */
    @Named("stringToStatus")
    default Match.MatchStatus stringToStatus(String status) {
        try {
            return status != null ? Match.MatchStatus.valueOf(status) : Match.MatchStatus.SCHEDULED;
        } catch (IllegalArgumentException e) {
            return Match.MatchStatus.SCHEDULED;
        }
    }

    /**
     * Check if match is currently live
     */
    default Boolean isMatchLive(Match match) {
        return match != null && match.getStatus() == Match.MatchStatus.LIVE;
    }

    /**
     * Check if match is upcoming (scheduled and in future)
     */
    default Boolean isMatchUpcoming(Match match) {
        if (match == null || match.getMatchDate() == null) {
            return false;
        }
        return match.getStatus() == Match.MatchStatus.SCHEDULED
                && (match.getMatchDate().isAfter(LocalDate.now())
                || match.getMatchDate().isEqual(LocalDate.now()));
    }

    // ========================================================================
    // Update Mappings (for PATCH operations)
    // ========================================================================

    /**
     * Update existing Match entity from MatchDTO
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    void updateMatchFromDTO(MatchDTO dto, @MappingTarget Match entity);

    /**
     * Update existing Team entity from TeamDTO
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateTeamFromDTO(TeamDTO dto, @MappingTarget Team entity);
}
