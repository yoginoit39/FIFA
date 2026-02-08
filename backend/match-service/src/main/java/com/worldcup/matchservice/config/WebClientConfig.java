package com.worldcup.matchservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient configuration for external API calls
 */
@Configuration
public class WebClientConfig {

    @Value("${external.api.football.base-url}")
    private String footballApiBaseUrl;

    @Value("${external.api.football.api-key}")
    private String footballApiKey;

    @Value("${external.api.football.host}")
    private String footballApiHost;

    @Value("${external.api.thesportsdb.base-url}")
    private String sportsDbBaseUrl;

    /**
     * WebClient for API-FOOTBALL (RapidAPI)
     */
    @Bean(name = "footballApiWebClient")
    public WebClient footballApiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        return WebClient.builder()
                .baseUrl(footballApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("x-rapidapi-key", footballApiKey)
                .defaultHeader("x-rapidapi-host", footballApiHost)
                .build();
    }

    /**
     * WebClient for TheSportsDB (fallback)
     */
    @Bean(name = "sportsDbWebClient")
    public WebClient sportsDbWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        return WebClient.builder()
                .baseUrl(sportsDbBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
