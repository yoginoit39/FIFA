package com.worldcup.matchservice.service;

import com.worldcup.matchservice.dto.MatchDTO;
import com.worldcup.matchservice.dto.MatchDetailDTO;
import com.worldcup.matchservice.entity.Match;
import com.worldcup.matchservice.entity.Match.MatchStatus;
import com.worldcup.matchservice.exception.ResourceNotFoundException;
import com.worldcup.matchservice.mapper.MatchMapper;
import com.worldcup.matchservice.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Match operations
 * Handles business logic, caching, and data validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    /**
     * Get all matches with pagination
     */
    @Cacheable(value = "matches", key = "'page_' + #page + '_size_' + #size")
    public Page<MatchDTO> getAllMatches(int page, int size) {
        log.debug("Fetching matches - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matchPage = matchRepository.findAllByOrderByMatchDateAsc(pageable);
        return matchPage.map(matchMapper::toMatchDTO);
    }

    /**
     * Get match by ID with full details
     */
    @Cacheable(value = "matches", key = "'detail_' + #id")
    public MatchDetailDTO getMatchById(Long id) {
        log.debug("Fetching match by ID: {}", id);
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));
        return matchMapper.toMatchDetailDTO(match);
    }

    /**
     * Get upcoming matches (scheduled, future dates)
     */
    @Cacheable(value = "upcomingMatches", key = "'all'")
    public List<MatchDTO> getUpcomingMatches() {
        log.debug("Fetching upcoming matches");
        LocalDate today = LocalDate.now();
        List<Match> matches = matchRepository.findUpcomingMatches(today);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by specific date
     */
    @Cacheable(value = "matchesByDate", key = "#date")
    public List<MatchDTO> getMatchesByDate(LocalDate date) {
        log.debug("Fetching matches by date: {}", date);
        List<Match> matches = matchRepository.findByMatchDateOrderByMatchTimeAsc(date);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by date range
     */
    public List<MatchDTO> getMatchesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching matches from {} to {}", startDate, endDate);
        List<Match> matches = matchRepository.findByDateRange(startDate, endDate);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by team (home or away)
     */
    public List<MatchDTO> getMatchesByTeam(Long teamId) {
        log.debug("Fetching matches for team ID: {}", teamId);
        List<Match> matches = matchRepository.findByTeamId(teamId);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by stadium
     */
    public List<MatchDTO> getMatchesByStadium(Long stadiumId) {
        log.debug("Fetching matches for stadium ID: {}", stadiumId);
        List<Match> matches = matchRepository.findByStadiumIdOrderByMatchDateAsc(stadiumId);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by round
     */
    public List<MatchDTO> getMatchesByRound(String round) {
        log.debug("Fetching matches for round: {}", round);
        List<Match> matches = matchRepository.findByRoundOrderByMatchDateAsc(round);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by group
     */
    public List<MatchDTO> getMatchesByGroup(String groupName) {
        log.debug("Fetching matches for group: {}", groupName);
        List<Match> matches = matchRepository.findByGroupNameOrderByMatchDateAsc(groupName);
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get live matches
     */
    @Cacheable(value = "matches", key = "'live'")
    public List<MatchDTO> getLiveMatches() {
        log.debug("Fetching live matches");
        List<Match> matches = matchRepository.findLiveMatches();
        return matchMapper.toMatchDTOList(matches);
    }

    /**
     * Get matches by status
     */
    public List<MatchDTO> getMatchesByStatus(String status) {
        log.debug("Fetching matches with status: {}", status);
        try {
            MatchStatus matchStatus = MatchStatus.valueOf(status.toUpperCase());
            List<Match> matches = matchRepository.findByStatusOrderByMatchDateAsc(matchStatus);
            return matchMapper.toMatchDTOList(matches);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid match status: " + status);
        }
    }

    /**
     * Create new match
     */
    @Transactional
    @CacheEvict(value = {"matches", "upcomingMatches", "matchesByDate"}, allEntries = true)
    public MatchDTO createMatch(MatchDTO matchDTO) {
        log.info("Creating new match: {} vs {}",
                matchDTO.getHomeTeam().getName(),
                matchDTO.getAwayTeam().getName());

        // Validate teams are different
        if (matchDTO.getHomeTeam().getId().equals(matchDTO.getAwayTeam().getId())) {
            throw new IllegalArgumentException("Home team and away team cannot be the same");
        }

        Match match = matchMapper.toMatchEntity(matchDTO);
        Match savedMatch = matchRepository.save(match);
        log.info("Match created successfully with ID: {}", savedMatch.getId());

        return matchMapper.toMatchDTO(savedMatch);
    }

    /**
     * Update existing match
     */
    @Transactional
    @CacheEvict(value = {"matches", "upcomingMatches", "matchesByDate"}, allEntries = true)
    public MatchDTO updateMatch(Long id, MatchDTO matchDTO) {
        log.info("Updating match with ID: {}", id);

        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));

        matchMapper.updateMatchFromDTO(matchDTO, existingMatch);
        Match updatedMatch = matchRepository.save(existingMatch);
        log.info("Match updated successfully with ID: {}", updatedMatch.getId());

        return matchMapper.toMatchDTO(updatedMatch);
    }

    /**
     * Update match score
     */
    @Transactional
    @CacheEvict(value = {"matches", "upcomingMatches", "matchesByDate"}, allEntries = true)
    public MatchDTO updateMatchScore(Long id, Integer homeScore, Integer awayScore) {
        log.info("Updating score for match ID: {} - {}:{}", id, homeScore, awayScore);

        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);

        // If match has a score, it's likely finished or live
        if (homeScore != null && awayScore != null && homeScore + awayScore > 0) {
            if (match.getStatus() == MatchStatus.SCHEDULED) {
                match.setStatus(MatchStatus.LIVE);
            }
        }

        Match updatedMatch = matchRepository.save(match);
        return matchMapper.toMatchDTO(updatedMatch);
    }

    /**
     * Update match status
     */
    @Transactional
    @CacheEvict(value = {"matches", "upcomingMatches", "matchesByDate"}, allEntries = true)
    public MatchDTO updateMatchStatus(Long id, String status) {
        log.info("Updating status for match ID: {} to {}", id, status);

        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));

        try {
            MatchStatus matchStatus = MatchStatus.valueOf(status.toUpperCase());
            match.setStatus(matchStatus);
            Match updatedMatch = matchRepository.save(match);
            return matchMapper.toMatchDTO(updatedMatch);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid match status: " + status);
        }
    }

    /**
     * Delete match
     */
    @Transactional
    @CacheEvict(value = {"matches", "upcomingMatches", "matchesByDate"}, allEntries = true)
    public void deleteMatch(Long id) {
        log.info("Deleting match with ID: {}", id);

        if (!matchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Match not found with ID: " + id);
        }

        matchRepository.deleteById(id);
        log.info("Match deleted successfully with ID: {}", id);
    }

    /**
     * Save or update match (used by data sync)
     */
    @Transactional
    public Match saveOrUpdateMatch(Match match) {
        // Check if match exists by external API ID
        if (match.getExternalApiId() != null) {
            return matchRepository.findByExternalApiId(match.getExternalApiId())
                    .map(existingMatch -> {
                        // Update existing match
                        existingMatch.setMatchDate(match.getMatchDate());
                        existingMatch.setMatchTime(match.getMatchTime());
                        existingMatch.setStatus(match.getStatus());
                        existingMatch.setHomeScore(match.getHomeScore());
                        existingMatch.setAwayScore(match.getAwayScore());
                        existingMatch.setHomeTeam(match.getHomeTeam());
                        existingMatch.setAwayTeam(match.getAwayTeam());
                        existingMatch.setStadiumId(match.getStadiumId());
                        existingMatch.setRound(match.getRound());
                        existingMatch.setGroupName(match.getGroupName());
                        existingMatch.setVenueName(match.getVenueName());
                        existingMatch.setVenueCity(match.getVenueCity());
                        existingMatch.setVenueCountry(match.getVenueCountry());
                        return matchRepository.save(existingMatch);
                    })
                    .orElseGet(() -> matchRepository.save(match));
        }

        return matchRepository.save(match);
    }

    /**
     * Count total matches
     */
    public long countMatches() {
        return matchRepository.count();
    }

    /**
     * Count matches by status
     */
    public long countMatchesByStatus(String status) {
        try {
            MatchStatus matchStatus = MatchStatus.valueOf(status.toUpperCase());
            return matchRepository.countByStatus(matchStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid match status: " + status);
        }
    }

    /**
     * Clear match cache
     */
    @CacheEvict(value = {"matches", "upcomingMatches", "matchesByDate"}, allEntries = true)
    public void clearCache() {
        log.info("Match cache cleared");
    }
}
