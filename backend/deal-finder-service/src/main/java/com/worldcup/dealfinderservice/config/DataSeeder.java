package com.worldcup.dealfinderservice.config;

import com.worldcup.dealfinderservice.entity.DealScore;
import com.worldcup.dealfinderservice.entity.MatchDealSummary;
import com.worldcup.dealfinderservice.entity.Provider;
import com.worldcup.dealfinderservice.repository.DealScoreRepository;
import com.worldcup.dealfinderservice.repository.MatchDealSummaryRepository;
import com.worldcup.dealfinderservice.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProviderRepository providerRepository;
    private final DealScoreRepository dealScoreRepository;
    private final MatchDealSummaryRepository matchDealSummaryRepository;

    private static final long[] MATCH_IDS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};

    @Override
    @Transactional
    public void run(String... args) {
        List<DealScore> existing = dealScoreRepository.findTopDeals();
        if (!existing.isEmpty()) {
            log.info("Deal scores already seeded ({} records), skipping", existing.size());
            return;
        }

        List<Provider> providers = providerRepository.findByIsActiveTrueOrderByPriorityAsc();
        if (providers.isEmpty()) {
            log.warn("No providers found, skipping deal seeding");
            return;
        }

        log.info("Seeding simulated deal scores for {} matches across {} providers",
                MATCH_IDS.length, providers.size());

        Random rng = new Random(42);

        for (long matchId : MATCH_IDS) {
            BigDecimal lowestPrice = null;
            BigDecimal highestPrice = null;
            BigDecimal priceSum = BigDecimal.ZERO;
            DealScore bestDeal = null;
            int providerCount = 0;

            // Pick 3-6 random providers per match
            final int numProviders = 3 + rng.nextInt(4);
            final int totalProviders = providers.size();
            List<Provider> matchProviders = providers.stream()
                    .filter(p -> rng.nextDouble() < (double) numProviders / totalProviders)
                    .limit(numProviders)
                    .toList();
            if (matchProviders.isEmpty()) {
                matchProviders = providers.subList(0, Math.min(3, providers.size()));
            }

            // Generate base price range depending on match round
            double baseMin = matchId <= 12 ? 80 : matchId <= 16 ? 150 : 250;
            double baseMax = matchId <= 12 ? 250 : matchId <= 16 ? 450 : 800;

            for (Provider provider : matchProviders) {
                double price = baseMin + rng.nextDouble() * (baseMax - baseMin);
                BigDecimal currentPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);

                double marketAvg = (baseMin + baseMax) / 2;
                BigDecimal marketAverage = BigDecimal.valueOf(marketAvg).setScale(2, RoundingMode.HALF_UP);

                double ratio = price / marketAvg;
                int dealScore = Math.max(0, Math.min(100, (int) (100 - (ratio * 50))));

                BigDecimal savingsPercentage = marketAverage.subtract(currentPrice)
                        .divide(marketAverage, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

                String[] trends = {"STABLE", "DOWN", "UP", "STABLE", "STABLE", "DOWN"};
                String priceTrend = trends[rng.nextInt(trends.length)];
                BigDecimal trendPct = BigDecimal.valueOf(-5 + rng.nextDouble() * 10).setScale(2, RoundingMode.HALF_UP);

                BigDecimal low = currentPrice.multiply(BigDecimal.valueOf(0.85 + rng.nextDouble() * 0.1)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal high = currentPrice.multiply(BigDecimal.valueOf(1.05 + rng.nextDouble() * 0.15)).setScale(2, RoundingMode.HALF_UP);

                String bestTimeToBuy;
                String recommendation;
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
                } else {
                    bestTimeToBuy = "NOW";
                    recommendation = "Fair price. Good time to purchase.";
                }

                String bookingUrl = provider.getWebsiteUrl() + "/event/fifa-world-cup-2026-match-" + matchId;

                DealScore ds = DealScore.builder()
                        .matchId(matchId)
                        .provider(provider)
                        .category("GENERAL")
                        .dealScore(dealScore)
                        .currentPrice(currentPrice)
                        .marketAverage(marketAverage)
                        .savingsPercentage(savingsPercentage)
                        .priceTrend(priceTrend)
                        .trendPercentage(trendPct)
                        .price7dLow(low)
                        .price7dHigh(high)
                        .bestTimeToBuy(bestTimeToBuy)
                        .recommendation(recommendation)
                        .bookingUrl(bookingUrl)
                        .lastComputedAt(LocalDateTime.now())
                        .build();

                dealScoreRepository.save(ds);
                providerCount++;
                priceSum = priceSum.add(currentPrice);

                if (lowestPrice == null || currentPrice.compareTo(lowestPrice) < 0) lowestPrice = currentPrice;
                if (highestPrice == null || currentPrice.compareTo(highestPrice) > 0) highestPrice = currentPrice;
                if (bestDeal == null || dealScore > bestDeal.getDealScore()) bestDeal = ds;
            }

            // Create match summary
            if (bestDeal != null && providerCount > 0) {
                BigDecimal avg = priceSum.divide(BigDecimal.valueOf(providerCount), 2, RoundingMode.HALF_UP);
                MatchDealSummary summary = MatchDealSummary.builder()
                        .matchId(matchId)
                        .category("GENERAL")
                        .lowestPrice(lowestPrice)
                        .highestPrice(highestPrice)
                        .averagePrice(avg)
                        .bestProvider(bestDeal.getProvider())
                        .bestDealScore(bestDeal.getDealScore())
                        .numProviders(providerCount)
                        .overallTrend(bestDeal.getPriceTrend())
                        .bestTimeToBuy(bestDeal.getBestTimeToBuy())
                        .lastComputedAt(LocalDateTime.now())
                        .build();
                matchDealSummaryRepository.save(summary);
            }
        }

        log.info("Deal data seeding complete");
    }
}
