package com.worldcup.dealfinderservice.service;

import com.worldcup.dealfinderservice.dto.*;
import com.worldcup.dealfinderservice.entity.DealScore;
import com.worldcup.dealfinderservice.entity.MatchDealSummary;
import com.worldcup.dealfinderservice.entity.PriceSnapshot;
import com.worldcup.dealfinderservice.mapper.DealScoreMapper;
import com.worldcup.dealfinderservice.mapper.MatchDealSummaryMapper;
import com.worldcup.dealfinderservice.mapper.PriceSnapshotMapper;
import com.worldcup.dealfinderservice.repository.DealScoreRepository;
import com.worldcup.dealfinderservice.repository.MatchDealSummaryRepository;
import com.worldcup.dealfinderservice.repository.PriceSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DealComparisonService {

    private final DealScoreRepository dealScoreRepository;
    private final MatchDealSummaryRepository matchDealSummaryRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final DealScoreMapper dealScoreMapper;
    private final MatchDealSummaryMapper matchDealSummaryMapper;
    private final PriceSnapshotMapper priceSnapshotMapper;

    @Cacheable(value = "dealComparison", key = "#matchId")
    public DealComparisonDTO getDealsForMatch(Long matchId) {
        log.debug("Fetching deal comparison for match ID: {}", matchId);

        List<DealScore> dealScores = dealScoreRepository.findByMatchIdOrderByPriceAsc(matchId);
        List<DealScoreDTO> dealDTOs = dealScoreMapper.toDTOList(dealScores);

        MatchDealSummaryDTO summaryDTO = null;
        MatchDealSummary summary = matchDealSummaryRepository
                .findByMatchIdAndCategory(matchId, "GENERAL").orElse(null);
        if (summary != null) {
            summaryDTO = matchDealSummaryMapper.toDTO(summary);
        }

        LocalDateTime lastUpdated = dealScores.stream()
                .map(DealScore::getLastComputedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return DealComparisonDTO.builder()
                .matchId(matchId)
                .summary(summaryDTO)
                .deals(dealDTOs)
                .lastUpdated(lastUpdated)
                .build();
    }

    public DealScoreDTO getCheapestDeal(Long matchId) {
        log.debug("Fetching cheapest deal for match ID: {}", matchId);
        List<DealScore> deals = dealScoreRepository.findByMatchIdOrderByPriceAsc(matchId);
        if (deals.isEmpty()) {
            return null;
        }
        return dealScoreMapper.toDTO(deals.get(0));
    }

    @Cacheable(value = "topDeals", key = "'top_' + #limit")
    public List<DealScoreDTO> getTopDeals(int limit) {
        log.debug("Fetching top {} deals", limit);
        List<DealScore> topDeals = dealScoreRepository.findTopDeals();
        return topDeals.stream()
                .limit(limit)
                .map(dealScoreMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "priceHistory", key = "#matchId")
    public List<PriceSnapshotDTO> getPriceHistory(Long matchId) {
        log.debug("Fetching price history for match ID: {}", matchId);
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<PriceSnapshot> snapshots = priceSnapshotRepository.findByMatchIdSince(matchId, sevenDaysAgo);
        return priceSnapshotMapper.toDTOList(snapshots);
    }

    @Cacheable(value = "dealSummary", key = "'all'")
    public List<MatchDealSummaryDTO> getAllMatchSummaries() {
        log.debug("Fetching all match deal summaries");
        List<MatchDealSummary> summaries = matchDealSummaryRepository.findAllByOrderByLowestPriceAsc();
        return matchDealSummaryMapper.toDTOList(summaries);
    }
}
