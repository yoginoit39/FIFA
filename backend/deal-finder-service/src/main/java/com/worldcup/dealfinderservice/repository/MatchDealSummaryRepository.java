package com.worldcup.dealfinderservice.repository;

import com.worldcup.dealfinderservice.entity.MatchDealSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchDealSummaryRepository extends JpaRepository<MatchDealSummary, Long> {

    List<MatchDealSummary> findByMatchId(Long matchId);

    Optional<MatchDealSummary> findByMatchIdAndCategory(Long matchId, String category);

    List<MatchDealSummary> findAllByOrderByLowestPriceAsc();
}
