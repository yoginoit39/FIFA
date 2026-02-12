package com.worldcup.dealfinderservice.service;

import com.worldcup.dealfinderservice.entity.DealScore;
import com.worldcup.dealfinderservice.entity.MatchDealSummary;
import com.worldcup.dealfinderservice.entity.PriceSnapshot;
import com.worldcup.dealfinderservice.repository.DealScoreRepository;
import com.worldcup.dealfinderservice.repository.MatchDealSummaryRepository;
import com.worldcup.dealfinderservice.repository.PriceSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealScoringService {

    private final PriceSnapshotRepository priceSnapshotRepository;
    private final DealScoreRepository dealScoreRepository;
    private final MatchDealSummaryRepository matchDealSummaryRepository;

    @Transactional
    @CacheEvict(value = {"dealComparison", "dealSummary", "topDeals"}, allEntries = true)
    public void computeAllScores() {
        log.info("Computing deal scores for all matches");
        List<Long> matchIds = priceSnapshotRepository.findDistinctMatchIds();

        for (Long matchId : matchIds) {
            computeScoresForMatch(matchId);
        }

        log.info("Deal score computation complete for {} matches", matchIds.size());
    }

    @Transactional
    public void computeScoresForMatch(Long matchId) {
        List<PriceSnapshot> latestSnapshots = priceSnapshotRepository.findLatestByMatchId(matchId);
        if (latestSnapshots.isEmpty()) {
            return;
        }

        // Calculate market average
        BigDecimal totalPriceSum = latestSnapshots.stream()
                .map(PriceSnapshot::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal marketAverage = totalPriceSum.divide(
                BigDecimal.valueOf(latestSnapshots.size()), 2, RoundingMode.HALF_UP);

        // Get 7-day price history for trend analysis
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<PriceSnapshot> recentHistory = priceSnapshotRepository.findByMatchIdSince(matchId, sevenDaysAgo);

        BigDecimal price7dLow = recentHistory.stream()
                .map(PriceSnapshot::getTotalPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal price7dHigh = recentHistory.stream()
                .map(PriceSnapshot::getTotalPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Group history by provider for trend calculation
        Map<Long, List<PriceSnapshot>> historyByProvider = recentHistory.stream()
                .collect(Collectors.groupingBy(ps -> ps.getProvider().getId()));

        // Score each provider
        BigDecimal lowestPrice = null;
        BigDecimal highestPrice = null;
        DealScore bestDeal = null;

        for (PriceSnapshot snapshot : latestSnapshots) {
            BigDecimal currentPrice = snapshot.getTotalPrice();

            // Deal score: how much below market average (0-100)
            int dealScore = computeDealScore(currentPrice, marketAverage);

            // Savings percentage
            BigDecimal savingsPercentage = BigDecimal.ZERO;
            if (marketAverage.compareTo(BigDecimal.ZERO) > 0) {
                savingsPercentage = marketAverage.subtract(currentPrice)
                        .divide(marketAverage, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            // Price trend
            String priceTrend = "STABLE";
            BigDecimal trendPercentage = BigDecimal.ZERO;
            List<PriceSnapshot> providerHistory = historyByProvider.get(snapshot.getProvider().getId());
            if (providerHistory != null && providerHistory.size() >= 2) {
                BigDecimal oldestPrice = providerHistory.get(providerHistory.size() - 1).getTotalPrice();
                BigDecimal newestPrice = providerHistory.get(0).getTotalPrice();
                if (oldestPrice.compareTo(BigDecimal.ZERO) > 0) {
                    trendPercentage = newestPrice.subtract(oldestPrice)
                            .divide(oldestPrice, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                    if (trendPercentage.compareTo(BigDecimal.valueOf(2)) > 0) {
                        priceTrend = "UP";
                    } else if (trendPercentage.compareTo(BigDecimal.valueOf(-2)) < 0) {
                        priceTrend = "DOWN";
                    }
                }
            }

            // Best time to buy recommendation
            String bestTimeToBuy = "NOW";
            String recommendation = "Good deal available.";
            if ("DOWN".equals(priceTrend)) {
                bestTimeToBuy = "WAIT";
                recommendation = "Prices are trending down. Consider waiting for a better deal.";
            } else if (dealScore >= 80) {
                bestTimeToBuy = "NOW";
                recommendation = "Great deal! This price is well below the market average.";
            } else if (dealScore >= 60) {
                bestTimeToBuy = "NOW";
                recommendation = "Good price. Worth buying now.";
            } else if ("UP".equals(priceTrend)) {
                bestTimeToBuy = "NOW";
                recommendation = "Prices are rising. Buy now before they go higher.";
            }

            // Upsert deal score
            DealScore dealScoreEntity = dealScoreRepository
                    .findByMatchIdAndProviderIdAndCategory(matchId, snapshot.getProvider().getId(), snapshot.getCategory())
                    .orElse(DealScore.builder()
                            .matchId(matchId)
                            .provider(snapshot.getProvider())
                            .category(snapshot.getCategory())
                            .build());

            dealScoreEntity.setDealScore(dealScore);
            dealScoreEntity.setCurrentPrice(currentPrice);
            dealScoreEntity.setMarketAverage(marketAverage);
            dealScoreEntity.setSavingsPercentage(savingsPercentage);
            dealScoreEntity.setPriceTrend(priceTrend);
            dealScoreEntity.setTrendPercentage(trendPercentage);
            dealScoreEntity.setPrice7dLow(price7dLow);
            dealScoreEntity.setPrice7dHigh(price7dHigh);
            dealScoreEntity.setBestTimeToBuy(bestTimeToBuy);
            dealScoreEntity.setRecommendation(recommendation);
            dealScoreEntity.setBookingUrl(snapshot.getBookingUrl());
            dealScoreEntity.setLastComputedAt(LocalDateTime.now());

            dealScoreRepository.save(dealScoreEntity);

            // Track best/worst for summary
            if (lowestPrice == null || currentPrice.compareTo(lowestPrice) < 0) {
                lowestPrice = currentPrice;
            }
            if (highestPrice == null || currentPrice.compareTo(highestPrice) > 0) {
                highestPrice = currentPrice;
            }
            if (bestDeal == null || dealScore > bestDeal.getDealScore()) {
                bestDeal = dealScoreEntity;
            }
        }

        // Upsert match deal summary
        if (bestDeal != null) {
            updateMatchSummary(matchId, "GENERAL", lowestPrice, highestPrice,
                    marketAverage, bestDeal, latestSnapshots.size());
        }
    }

    private int computeDealScore(BigDecimal currentPrice, BigDecimal marketAverage) {
        if (marketAverage.compareTo(BigDecimal.ZERO) <= 0) {
            return 50;
        }
        double ratio = currentPrice.doubleValue() / marketAverage.doubleValue();
        int score = (int) (100 - (ratio * 50));
        return Math.max(0, Math.min(100, score));
    }

    private void updateMatchSummary(Long matchId, String category, BigDecimal lowestPrice,
                                     BigDecimal highestPrice, BigDecimal averagePrice,
                                     DealScore bestDeal, int numProviders) {
        MatchDealSummary summary = matchDealSummaryRepository
                .findByMatchIdAndCategory(matchId, category)
                .orElse(MatchDealSummary.builder()
                        .matchId(matchId)
                        .category(category)
                        .build());

        summary.setLowestPrice(lowestPrice);
        summary.setHighestPrice(highestPrice);
        summary.setAveragePrice(averagePrice);
        summary.setBestProvider(bestDeal.getProvider());
        summary.setBestDealScore(bestDeal.getDealScore());
        summary.setNumProviders(numProviders);
        summary.setOverallTrend(bestDeal.getPriceTrend());
        summary.setBestTimeToBuy(bestDeal.getBestTimeToBuy());
        summary.setLastComputedAt(LocalDateTime.now());

        matchDealSummaryRepository.save(summary);
    }
}
