package com.worldcup.matchservice.repository;

import com.worldcup.matchservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Team entity
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Find team by external API ID
     */
    Optional<Team> findByExternalApiId(String externalApiId);

    /**
     * Find team by country
     */
    Optional<Team> findByCountry(String country);

    /**
     * Find all teams ordered by FIFA ranking
     */
    @Query("SELECT t FROM Team t ORDER BY t.fifaRanking ASC NULLS LAST")
    List<Team> findAllOrderedByRanking();

    /**
     * Check if team exists by external API ID
     */
    boolean existsByExternalApiId(String externalApiId);
}
