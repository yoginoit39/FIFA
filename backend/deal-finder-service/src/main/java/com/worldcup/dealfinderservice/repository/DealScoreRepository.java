package com.worldcup.dealfinderservice.repository;

import com.worldcup.dealfinderservice.entity.DealScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealScoreRepository extends JpaRepository<DealScore, Long> {

    List<DealScore> findByMatchIdOrderByDealScoreDesc(Long matchId);

    List<DealScore> findByMatchIdAndCategoryOrderByDealScoreDesc(Long matchId, String category);

    Optional<DealScore> findByMatchIdAndProviderIdAndCategory(Long matchId, Long providerId, String category);

    @Query("SELECT ds FROM DealScore ds ORDER BY ds.dealScore DESC")
    List<DealScore> findTopDeals();

    @Query("SELECT ds FROM DealScore ds WHERE ds.matchId = :matchId ORDER BY ds.currentPrice ASC")
    List<DealScore> findByMatchIdOrderByPriceAsc(@Param("matchId") Long matchId);
}
