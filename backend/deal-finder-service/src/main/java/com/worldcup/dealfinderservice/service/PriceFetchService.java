package com.worldcup.dealfinderservice.service;

import com.worldcup.dealfinderservice.client.TicketProviderClient;
import com.worldcup.dealfinderservice.entity.FetchLog;
import com.worldcup.dealfinderservice.entity.PriceSnapshot;
import com.worldcup.dealfinderservice.entity.Provider;
import com.worldcup.dealfinderservice.repository.FetchLogRepository;
import com.worldcup.dealfinderservice.repository.PriceSnapshotRepository;
import com.worldcup.dealfinderservice.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceFetchService {

    private final List<TicketProviderClient> providerClients;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final ProviderRepository providerRepository;
    private final FetchLogRepository fetchLogRepository;

    private static final String[] SEARCH_KEYWORDS = {
            "FIFA World Cup 2026",
            "World Cup Soccer 2026",
            "FIFA World Cup"
    };

    @Transactional
    @CacheEvict(value = {"dealComparison", "dealSummary", "topDeals", "priceHistory"}, allEntries = true)
    public int fetchAllPrices() {
        log.info("Starting price fetch from all providers");
        int totalFetched = 0;

        for (TicketProviderClient client : providerClients) {
            totalFetched += fetchFromProvider(client);
        }

        log.info("Price fetch complete. Total snapshots saved: {}", totalFetched);
        return totalFetched;
    }

    private int fetchFromProvider(TicketProviderClient client) {
        String providerName = client.getProviderName();
        LocalDateTime startedAt = LocalDateTime.now();
        Provider provider = providerRepository.findByName(providerName).orElse(null);

        try {
            log.info("Fetching prices from {}", providerName);
            int count = 0;

            for (String keyword : SEARCH_KEYWORDS) {
                List<PriceSnapshot> snapshots = client.fetchPrices(keyword);
                if (!snapshots.isEmpty()) {
                    priceSnapshotRepository.saveAll(snapshots);
                    count += snapshots.size();
                }
            }

            logFetch(provider, "SCHEDULED", "SUCCESS", count, null, startedAt);
            log.info("{}: saved {} price snapshots", providerName, count);
            return count;

        } catch (Exception e) {
            log.error("Error fetching from {}: {}", providerName, e.getMessage(), e);
            logFetch(provider, "SCHEDULED", "FAILED", 0, e.getMessage(), startedAt);
            return 0;
        }
    }

    private void logFetch(Provider provider, String fetchType, String status,
                          int recordsFetched, String errorMessage, LocalDateTime startedAt) {
        FetchLog fetchLog = FetchLog.builder()
                .provider(provider)
                .fetchType(fetchType)
                .status(status)
                .recordsFetched(recordsFetched)
                .errorMessage(errorMessage)
                .startedAt(startedAt)
                .completedAt(LocalDateTime.now())
                .durationMs(java.time.Duration.between(startedAt, LocalDateTime.now()).toMillis())
                .build();
        fetchLogRepository.save(fetchLog);
    }
}
