package com.worldcup.dealfinderservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldcup.dealfinderservice.entity.PriceSnapshot;
import com.worldcup.dealfinderservice.entity.Provider;
import com.worldcup.dealfinderservice.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketmasterClient implements TicketProviderClient {

    private final WebClient webClient;
    private final ProviderRepository providerRepository;
    private final ObjectMapper objectMapper;

    @Value("${external-api.ticketmaster.base-url}")
    private String baseUrl;

    @Value("${external-api.ticketmaster.api-key}")
    private String apiKey;

    @Override
    public String getProviderName() {
        return "Ticketmaster";
    }

    @Override
    public List<PriceSnapshot> fetchPrices(String keyword) {
        List<PriceSnapshot> snapshots = new ArrayList<>();

        try {
            String response = webClient.get()
                    .uri(baseUrl + "/events.json?keyword={keyword}&classificationName=Soccer&size=20&apikey={apiKey}",
                            keyword, apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                log.warn("Ticketmaster returned null response for keyword: {}", keyword);
                return snapshots;
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode embedded = root.path("_embedded");
            if (embedded.isMissingNode()) {
                log.info("No Ticketmaster events found for keyword: {}", keyword);
                return snapshots;
            }

            JsonNode events = embedded.path("events");
            Provider provider = providerRepository.findByName("Ticketmaster").orElse(null);
            if (provider == null) {
                log.error("Ticketmaster provider not found in database");
                return snapshots;
            }

            for (JsonNode event : events) {
                try {
                    JsonNode priceRanges = event.path("priceRanges");
                    if (priceRanges.isMissingNode() || !priceRanges.isArray() || priceRanges.isEmpty()) {
                        continue;
                    }

                    String eventName = event.path("name").asText("");
                    String eventUrl = event.path("url").asText("");
                    String eventId = event.path("id").asText("");

                    for (JsonNode priceRange : priceRanges) {
                        BigDecimal minPrice = BigDecimal.valueOf(priceRange.path("min").asDouble(0));
                        BigDecimal maxPrice = BigDecimal.valueOf(priceRange.path("max").asDouble(0));
                        String currency = priceRange.path("currency").asText("USD");

                        if (minPrice.compareTo(BigDecimal.ZERO) <= 0) {
                            continue;
                        }

                        BigDecimal feeAmount = minPrice.multiply(provider.getFeePercentage())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        BigDecimal totalPrice = minPrice.add(feeAmount);

                        PriceSnapshot snapshot = PriceSnapshot.builder()
                                .matchId(hashEventToMatchId(eventId))
                                .provider(provider)
                                .category("GENERAL")
                                .basePrice(minPrice)
                                .feeAmount(feeAmount)
                                .totalPrice(totalPrice)
                                .currency(currency)
                                .availabilityStatus("AVAILABLE")
                                .bookingUrl(eventUrl)
                                .sourceType("REAL_API")
                                .fetchedAt(LocalDateTime.now())
                                .build();

                        snapshots.add(snapshot);
                    }
                } catch (Exception e) {
                    log.warn("Error parsing Ticketmaster event: {}", e.getMessage());
                }
            }

            log.info("Ticketmaster: fetched {} price snapshots for keyword '{}'", snapshots.size(), keyword);

        } catch (Exception e) {
            log.error("Error fetching from Ticketmaster API: {}", e.getMessage(), e);
        }

        return snapshots;
    }

    private Long hashEventToMatchId(String eventId) {
        return (long) Math.abs(eventId.hashCode() % 100000);
    }
}
