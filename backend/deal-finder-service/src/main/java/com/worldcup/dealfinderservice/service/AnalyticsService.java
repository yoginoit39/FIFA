package com.worldcup.dealfinderservice.service;

import com.worldcup.dealfinderservice.dto.MarketOverviewDTO;
import com.worldcup.dealfinderservice.dto.TrendingMatchDTO;
import com.worldcup.dealfinderservice.entity.DealScore;
import com.worldcup.dealfinderservice.entity.MatchDealSummary;
import com.worldcup.dealfinderservice.repository.DealScoreRepository;
import com.worldcup.dealfinderservice.repository.MatchDealSummaryRepository;
import com.worldcup.dealfinderservice.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final DealScoreRepository dealScoreRepository;
    private final MatchDealSummaryRepository matchDealSummaryRepository;
    private final ProviderRepository providerRepository;

    @Cacheable(value = "marketOverview", key = "'overview'")
    public MarketOverviewDTO getMarketOverview() {
        log.debug("Computing market overview");

        List<DealScore> allDeals = dealScoreRepository.findTopDeals();
        List<MatchDealSummary> summaries = matchDealSummaryRepository.findAllByOrderByLowestPriceAsc();
        int providerCount = providerRepository.findByIsActiveTrueOrderByPriorityAsc().size();

        if (allDeals.isEmpty()) {
            return MarketOverviewDTO.builder()
                    .totalMatches(0)
                    .totalProviders(providerCount)
                    .totalDeals(0)
                    .build();
        }

        BigDecimal overallLowest = summaries.stream()
                .map(MatchDealSummary::getLowestPrice)
                .filter(p -> p != null)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal overallHighest = summaries.stream()
                .map(MatchDealSummary::getHighestPrice)
                .filter(p -> p != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal avgPrice = allDeals.stream()
                .map(DealScore::getCurrentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(allDeals.size()), 2, RoundingMode.HALF_UP);

        double avgScore = allDeals.stream()
                .mapToInt(DealScore::getDealScore)
                .average()
                .orElse(0.0);

        int hotDeals = (int) allDeals.stream()
                .filter(d -> d.getDealScore() >= 70)
                .count();

        long downCount = summaries.stream().filter(s -> "DOWN".equals(s.getOverallTrend())).count();
        long upCount = summaries.stream().filter(s -> "UP".equals(s.getOverallTrend())).count();
        long stableCount = summaries.stream().filter(s -> "STABLE".equals(s.getOverallTrend())).count();

        long buyNow = summaries.stream().filter(s -> "NOW".equals(s.getBestTimeToBuy())).count();
        double buyNowPct = summaries.isEmpty() ? 0.0 :
                (double) buyNow / summaries.size() * 100;

        return MarketOverviewDTO.builder()
                .totalMatches(summaries.size())
                .totalProviders(providerCount)
                .totalDeals(allDeals.size())
                .overallLowestPrice(overallLowest)
                .overallHighestPrice(overallHighest)
                .averagePrice(avgPrice)
                .averageDealScore(Math.round(avgScore * 10.0) / 10.0)
                .hotDealCount(hotDeals)
                .pricesDownCount((int) downCount)
                .pricesUpCount((int) upCount)
                .pricesStableCount((int) stableCount)
                .buyNowPercentage(Math.round(buyNowPct * 10.0) / 10.0)
                .build();
    }

    @Cacheable(value = "trendingMatches", key = "'trending_' + #limit")
    public List<TrendingMatchDTO> getTrendingMatches(int limit) {
        log.debug("Computing trending matches, limit: {}", limit);

        List<MatchDealSummary> summaries = matchDealSummaryRepository.findAllByOrderByLowestPriceAsc();
        List<DealScore> allDeals = dealScoreRepository.findTopDeals();

        List<TrendingMatchDTO> trending = new ArrayList<>();

        for (MatchDealSummary summary : summaries) {
            // Compute popularity score based on multiple factors
            int popularity = computePopularityScore(summary, allDeals);

            BigDecimal priceSpread = BigDecimal.ZERO;
            if (summary.getHighestPrice() != null && summary.getLowestPrice() != null) {
                priceSpread = summary.getHighestPrice().subtract(summary.getLowestPrice());
            }

            BigDecimal maxSavings = allDeals.stream()
                    .filter(d -> d.getMatchId().equals(summary.getMatchId()))
                    .map(DealScore::getSavingsPercentage)
                    .filter(s -> s != null)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            String reason = buildTrendingReason(summary, popularity);

            trending.add(TrendingMatchDTO.builder()
                    .matchId(summary.getMatchId())
                    .popularityScore(popularity)
                    .bestDealScore(summary.getBestDealScore() != null ? summary.getBestDealScore() : 0)
                    .lowestPrice(summary.getLowestPrice())
                    .averagePrice(summary.getAveragePrice())
                    .priceSpread(priceSpread)
                    .numProviders(summary.getNumProviders() != null ? summary.getNumProviders() : 0)
                    .priceTrend(summary.getOverallTrend())
                    .bestProviderName(summary.getBestProvider() != null ?
                            summary.getBestProvider().getName() : null)
                    .maxSavingsPercentage(maxSavings)
                    .bestTimeToBuy(summary.getBestTimeToBuy())
                    .trendingReason(reason)
                    .build());
        }

        // Sort by popularity score descending, then add rank
        trending.sort(Comparator.comparingInt(TrendingMatchDTO::getPopularityScore).reversed());

        List<TrendingMatchDTO> result = trending.stream().limit(limit).toList();
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }

        return result;
    }

    @Cacheable(value = "priceDrops", key = "'drops_' + #limit")
    public List<TrendingMatchDTO> getBiggestPriceDrops(int limit) {
        log.debug("Computing biggest price drops, limit: {}", limit);

        List<MatchDealSummary> summaries = matchDealSummaryRepository.findAllByOrderByLowestPriceAsc();
        List<DealScore> allDeals = dealScoreRepository.findTopDeals();

        List<TrendingMatchDTO> drops = new ArrayList<>();

        for (MatchDealSummary summary : summaries) {
            BigDecimal maxSavings = allDeals.stream()
                    .filter(d -> d.getMatchId().equals(summary.getMatchId()))
                    .map(DealScore::getSavingsPercentage)
                    .filter(s -> s != null && s.compareTo(BigDecimal.ZERO) > 0)
                    .max(BigDecimal::compareTo)
                    .orElse(null);

            if (maxSavings == null) continue;

            BigDecimal priceSpread = BigDecimal.ZERO;
            if (summary.getHighestPrice() != null && summary.getLowestPrice() != null) {
                priceSpread = summary.getHighestPrice().subtract(summary.getLowestPrice());
            }

            drops.add(TrendingMatchDTO.builder()
                    .matchId(summary.getMatchId())
                    .bestDealScore(summary.getBestDealScore() != null ? summary.getBestDealScore() : 0)
                    .lowestPrice(summary.getLowestPrice())
                    .averagePrice(summary.getAveragePrice())
                    .priceSpread(priceSpread)
                    .numProviders(summary.getNumProviders() != null ? summary.getNumProviders() : 0)
                    .priceTrend(summary.getOverallTrend())
                    .bestProviderName(summary.getBestProvider() != null ?
                            summary.getBestProvider().getName() : null)
                    .maxSavingsPercentage(maxSavings)
                    .bestTimeToBuy(summary.getBestTimeToBuy())
                    .trendingReason("Save up to " + maxSavings.setScale(0, RoundingMode.HALF_UP) + "% vs market average")
                    .build());
        }

        drops.sort(Comparator.comparing(TrendingMatchDTO::getMaxSavingsPercentage).reversed());

        List<TrendingMatchDTO> result = drops.stream().limit(limit).toList();
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }

        return result;
    }

    private int computePopularityScore(MatchDealSummary summary, List<DealScore> allDeals) {
        int score = 0;

        // Factor 1: Deal score (max 35 points)
        if (summary.getBestDealScore() != null) {
            score += (int) (summary.getBestDealScore() * 0.35);
        }

        // Factor 2: Number of providers (max 20 points)
        if (summary.getNumProviders() != null) {
            score += Math.min(20, summary.getNumProviders() * 4);
        }

        // Factor 3: Price trend bonus (max 15 points)
        if ("DOWN".equals(summary.getOverallTrend())) {
            score += 15;
        } else if ("STABLE".equals(summary.getOverallTrend())) {
            score += 8;
        }

        // Factor 4: Savings available (max 20 points)
        BigDecimal maxSavings = allDeals.stream()
                .filter(d -> d.getMatchId().equals(summary.getMatchId()))
                .map(DealScore::getSavingsPercentage)
                .filter(s -> s != null && s.compareTo(BigDecimal.ZERO) > 0)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        score += Math.min(20, maxSavings.intValue());

        // Factor 5: Buy now recommendation (10 points)
        if ("NOW".equals(summary.getBestTimeToBuy())) {
            score += 10;
        }

        return Math.min(100, score);
    }

    private String buildTrendingReason(MatchDealSummary summary, int popularity) {
        if (popularity >= 80) {
            return "Hot deal with excellent savings and strong provider competition";
        } else if ("DOWN".equals(summary.getOverallTrend())) {
            return "Prices trending down - good time to watch for deals";
        } else if (summary.getBestDealScore() != null && summary.getBestDealScore() >= 70) {
            return "High deal score with competitive pricing from multiple providers";
        } else if (summary.getNumProviders() != null && summary.getNumProviders() >= 4) {
            return "Strong provider competition driving better prices";
        } else {
            return "Active market with multiple pricing options available";
        }
    }
}
