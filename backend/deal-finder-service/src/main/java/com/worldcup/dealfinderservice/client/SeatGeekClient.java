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
public class SeatGeekClient implements TicketProviderClient {

    private final WebClient webClient;
    private final ProviderRepository providerRepository;
    private final ObjectMapper objectMapper;

    @Value("${external-api.seatgeek.base-url}")
    private String baseUrl;

    @Value("${external-api.seatgeek.client-id}")
    private String clientId;

    @Override
    public String getProviderName() {
        return "SeatGeek";
    }

    @Override
    public List<PriceSnapshot> fetchPrices(String keyword) {
        List<PriceSnapshot> snapshots = new ArrayList<>();

        try {
            String response = webClient.get()
                    .uri(baseUrl + "/events?q={keyword}&per_page=20&client_id={clientId}",
                            keyword, clientId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                log.warn("SeatGeek returned null response for keyword: {}", keyword);
                return snapshots;
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode events = root.path("events");
            if (events.isMissingNode() || !events.isArray()) {
                log.info("No SeatGeek events found for keyword: {}", keyword);
                return snapshots;
            }

            Provider provider = providerRepository.findByName("SeatGeek").orElse(null);
            if (provider == null) {
                log.error("SeatGeek provider not found in database");
                return snapshots;
            }

            for (JsonNode event : events) {
                try {
                    JsonNode stats = event.path("stats");
                    if (stats.isMissingNode()) {
                        continue;
                    }

                    double lowestPrice = stats.path("lowest_price").asDouble(0);
                    if (lowestPrice <= 0) {
                        continue;
                    }

                    String eventUrl = event.path("url").asText("");
                    String eventId = String.valueOf(event.path("id").asLong(0));

                    BigDecimal basePrice = BigDecimal.valueOf(lowestPrice);
                    BigDecimal feeAmount = basePrice.multiply(provider.getFeePercentage())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    BigDecimal totalPrice = basePrice.add(feeAmount);

                    PriceSnapshot snapshot = PriceSnapshot.builder()
                            .matchId(hashEventToMatchId(eventId))
                            .provider(provider)
                            .category("GENERAL")
                            .basePrice(basePrice)
                            .feeAmount(feeAmount)
                            .totalPrice(totalPrice)
                            .currency("USD")
                            .availabilityStatus("AVAILABLE")
                            .bookingUrl(eventUrl)
                            .sourceType("REAL_API")
                            .fetchedAt(LocalDateTime.now())
                            .build();

                    snapshots.add(snapshot);
                } catch (Exception e) {
                    log.warn("Error parsing SeatGeek event: {}", e.getMessage());
                }
            }

            log.info("SeatGeek: fetched {} price snapshots for keyword '{}'", snapshots.size(), keyword);

        } catch (Exception e) {
            log.error("Error fetching from SeatGeek API: {}", e.getMessage(), e);
        }

        return snapshots;
    }

    private Long hashEventToMatchId(String eventId) {
        return (long) Math.abs(eventId.hashCode() % 100000);
    }
}
