package com.worldcup.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Gateway Configuration for routing requests to backend microservices
 */
@Configuration
public class GatewayConfig {

    @Value("${services.match-service.url:http://match-service:8081}")
    private String matchServiceUrl;

    @Value("${services.stadium-service.url:http://stadium-service:8082}")
    private String stadiumServiceUrl;

    @Value("${services.ticket-service.url:http://ticket-service:8083}")
    private String ticketServiceUrl;

    @Value("${services.deal-finder.url:http://deal-finder-service:8084}")
    private String dealFinderServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(WebClient webClient) {
        return route(path("/api/matches/**").or(path("/api/teams/**")).or(path("/api/admin/**")),
                request -> proxyRequest(request, matchServiceUrl, webClient))
            .andRoute(path("/api/stadiums/**"),
                request -> proxyRequest(request, stadiumServiceUrl, webClient))
            .andRoute(path("/api/tickets/**"),
                request -> proxyRequest(request, ticketServiceUrl, webClient))
            .andRoute(path("/api/deals/**"),
                request -> proxyRequest(request, dealFinderServiceUrl, webClient));
    }

    private Mono<ServerResponse> proxyRequest(ServerRequest request, String targetUrl, WebClient webClient) {
        String path = request.path();
        String query = request.uri().getQuery();
        String fullPath = path + (query != null ? "?" + query : "");

        return webClient.method(request.method())
                .uri(targetUrl + fullPath)
                .headers(headers -> {
                    copyHeaders(request.headers().asHttpHeaders(), headers);
                    headers.remove(HttpHeaders.ACCEPT_ENCODING);
                })
                .retrieve()
                .toEntity(String.class)
                .flatMap(responseEntity -> {
                    String body = responseEntity.getBody();
                    ServerResponse.BodyBuilder builder = ServerResponse.status(responseEntity.getStatusCode())
                            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                            .header(HttpHeaders.CONTENT_TYPE, "application/json");
                    if (body != null && !body.isEmpty()) {
                        return builder.bodyValue(body);
                    }
                    return builder.build();
                });
    }

    private void copyHeaders(HttpHeaders source, HttpHeaders target) {
        source.forEach((name, values) -> {
            if (!isHopByHopHeader(name)) {
                values.forEach(value -> target.add(name, value));
            }
        });
    }

    private boolean isHopByHopHeader(String headerName) {
        return headerName.equalsIgnoreCase(HttpHeaders.HOST) ||
               headerName.equalsIgnoreCase(HttpHeaders.CONNECTION) ||
               headerName.equalsIgnoreCase("Keep-Alive") ||
               headerName.equalsIgnoreCase(HttpHeaders.TRANSFER_ENCODING) ||
               headerName.equalsIgnoreCase("TE") ||
               headerName.equalsIgnoreCase("Trailer") ||
               headerName.equalsIgnoreCase(HttpHeaders.UPGRADE);
    }
}
