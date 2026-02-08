package com.worldcup.matchservice.service;

import com.worldcup.matchservice.dto.TeamDTO;
import com.worldcup.matchservice.entity.Team;
import com.worldcup.matchservice.exception.ResourceNotFoundException;
import com.worldcup.matchservice.mapper.MatchMapper;
import com.worldcup.matchservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Team operations
 * Handles business logic and caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MatchMapper matchMapper;

    /**
     * Get all teams ordered by FIFA ranking
     */
    @Cacheable(value = "teams", key = "'all'")
    public List<TeamDTO> getAllTeams() {
        log.debug("Fetching all teams from database");
        List<Team> teams = teamRepository.findAllOrderedByRanking();
        return matchMapper.toTeamDTOList(teams);
    }

    /**
     * Get team by ID
     */
    @Cacheable(value = "teams", key = "#id")
    public TeamDTO getTeamById(Long id) {
        log.debug("Fetching team by ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        return matchMapper.toTeamDTO(team);
    }

    /**
     * Get team by country
     */
    @Cacheable(value = "teams", key = "'country_' + #country")
    public TeamDTO getTeamByCountry(String country) {
        log.debug("Fetching team by country: {}", country);
        Team team = teamRepository.findByCountry(country)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found for country: " + country));
        return matchMapper.toTeamDTO(team);
    }

    /**
     * Get team by external API ID
     */
    @Cacheable(value = "teams", key = "'external_' + #externalApiId")
    public TeamDTO getTeamByExternalApiId(String externalApiId) {
        log.debug("Fetching team by external API ID: {}", externalApiId);
        Team team = teamRepository.findByExternalApiId(externalApiId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with external API ID: " + externalApiId));
        return matchMapper.toTeamDTO(team);
    }

    /**
     * Create new team
     */
    @Transactional
    @CacheEvict(value = "teams", allEntries = true)
    public TeamDTO createTeam(TeamDTO teamDTO) {
        log.info("Creating new team: {}", teamDTO.getName());

        // Check if team already exists by external API ID
        if (teamDTO.getExternalApiId() != null &&
                teamRepository.existsByExternalApiId(teamDTO.getExternalApiId())) {
            throw new IllegalArgumentException("Team already exists with external API ID: " + teamDTO.getExternalApiId());
        }

        Team team = matchMapper.toTeamEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        log.info("Team created successfully with ID: {}", savedTeam.getId());

        return matchMapper.toTeamDTO(savedTeam);
    }

    /**
     * Update existing team
     */
    @Transactional
    @CacheEvict(value = "teams", allEntries = true)
    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        log.info("Updating team with ID: {}", id);

        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        matchMapper.updateTeamFromDTO(teamDTO, existingTeam);
        Team updatedTeam = teamRepository.save(existingTeam);
        log.info("Team updated successfully with ID: {}", updatedTeam.getId());

        return matchMapper.toTeamDTO(updatedTeam);
    }

    /**
     * Delete team
     */
    @Transactional
    @CacheEvict(value = "teams", allEntries = true)
    public void deleteTeam(Long id) {
        log.info("Deleting team with ID: {}", id);

        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with ID: " + id);
        }

        teamRepository.deleteById(id);
        log.info("Team deleted successfully with ID: {}", id);
    }

    /**
     * Save or update team (used by data sync)
     */
    @Transactional
    public Team saveOrUpdateTeam(Team team) {
        // Check if team exists by external API ID
        if (team.getExternalApiId() != null) {
            return teamRepository.findByExternalApiId(team.getExternalApiId())
                    .map(existingTeam -> {
                        // Update existing team
                        existingTeam.setName(team.getName());
                        existingTeam.setCountry(team.getCountry());
                        existingTeam.setLogoUrl(team.getLogoUrl());
                        existingTeam.setFifaRanking(team.getFifaRanking());
                        return teamRepository.save(existingTeam);
                    })
                    .orElseGet(() -> teamRepository.save(team));
        }

        return teamRepository.save(team);
    }

    /**
     * Count total teams
     */
    public long countTeams() {
        return teamRepository.count();
    }

    /**
     * Clear team cache
     */
    @CacheEvict(value = "teams", allEntries = true)
    public void clearCache() {
        log.info("Team cache cleared");
    }
}
