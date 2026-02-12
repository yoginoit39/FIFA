package com.worldcup.dealfinderservice.repository;

import com.worldcup.dealfinderservice.entity.PriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {

    List<PriceSnapshot> findByMatchIdOrderByTotalPriceAsc(Long matchId);

    List<PriceSnapshot> findByMatchIdAndCategory(Long matchId, String category);

    @Query("SELECT ps FROM PriceSnapshot ps WHERE ps.matchId = :matchId " +
           "AND ps.fetchedAt = (SELECT MAX(ps2.fetchedAt) FROM PriceSnapshot ps2 " +
           "WHERE ps2.matchId = :matchId AND ps2.provider.id = ps.provider.id) " +
           "ORDER BY ps.totalPrice ASC")
    List<PriceSnapshot> findLatestByMatchId(@Param("matchId") Long matchId);

    @Query("SELECT ps FROM PriceSnapshot ps WHERE ps.matchId = :matchId " +
           "AND ps.provider.id = :providerId ORDER BY ps.fetchedAt DESC")
    List<PriceSnapshot> findPriceHistory(@Param("matchId") Long matchId,
                                         @Param("providerId") Long providerId);

    @Query("SELECT ps FROM PriceSnapshot ps WHERE ps.matchId = :matchId " +
           "AND ps.fetchedAt >= :since ORDER BY ps.fetchedAt DESC")
    List<PriceSnapshot> findByMatchIdSince(@Param("matchId") Long matchId,
                                           @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT ps.matchId FROM PriceSnapshot ps")
    List<Long> findDistinctMatchIds();

    void deleteByFetchedAtBefore(LocalDateTime cutoff);
}
