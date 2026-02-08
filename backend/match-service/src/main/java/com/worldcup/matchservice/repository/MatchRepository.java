package com.worldcup.matchservice.repository;

import com.worldcup.matchservice.entity.Match;
import com.worldcup.matchservice.entity.Match.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Match entity
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Find match by external API ID
     */
    Optional<Match> findByExternalApiId(String externalApiId);

    /**
     * Find all matches (paginated)
     */
    Page<Match> findAllByOrderByMatchDateAsc(Pageable pageable);

    /**
     * Find upcoming matches (scheduled status, future dates)
     */
    @Query("SELECT m FROM Match m WHERE m.status = 'SCHEDULED' AND m.matchDate >= :today ORDER BY m.matchDate ASC, m.matchTime ASC")
    List<Match> findUpcomingMatches(@Param("today") LocalDate today);

    /**
     * Find matches by date
     */
    List<Match> findByMatchDateOrderByMatchTimeAsc(LocalDate matchDate);

    /**
     * Find matches by date range
     */
    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :startDate AND :endDate ORDER BY m.matchDate ASC, m.matchTime ASC")
    List<Match> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find matches by team (either home or away)
     */
    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId ORDER BY m.matchDate ASC")
    List<Match> findByTeamId(@Param("teamId") Long teamId);

    /**
     * Find matches by stadium
     */
    List<Match> findByStadiumIdOrderByMatchDateAsc(Long stadiumId);

    /**
     * Find matches by round
     */
    List<Match> findByRoundOrderByMatchDateAsc(String round);

    /**
     * Find matches by group
     */
    List<Match> findByGroupNameOrderByMatchDateAsc(String groupName);

    /**
     * Find matches by status
     */
    List<Match> findByStatusOrderByMatchDateAsc(MatchStatus status);

    /**
     * Find live matches
     */
    @Query("SELECT m FROM Match m WHERE m.status = 'LIVE' ORDER BY m.matchDate DESC")
    List<Match> findLiveMatches();

    /**
     * Count matches by status
     */
    long countByStatus(MatchStatus status);

    /**
     * Check if match exists by external API ID
     */
    boolean existsByExternalApiId(String externalApiId);
}
