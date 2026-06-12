package com.budi.asteroid.tracker.client;


import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.client.dto.NasaNeoLookupResponse;
import com.budi.asteroid.tracker.config.NasaApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Component
public class NasaApiClientImpl implements NasaApiClient {

    private final RestClient restClient;
    private final NasaApiProperties properties;

    @Autowired
    public NasaApiClientImpl(NasaApiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Override
    public NasaNeoFeedResponse getNeoFeed(LocalDate startDate,LocalDate endDate) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/neo/rest/v1/feed")
                        .queryParam("start_date", startDate)
                        .queryParam("end_date", endDate)
                        .queryParam("api_key", properties.getKey())
                        .build())
                .retrieve()
                .body(NasaNeoFeedResponse.class);
    }

    @Override
    public NasaNeoLookupResponse getNeoLookup(String asteroidId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/neo/rest/v1/neo/{asteroidId}")
                        .queryParam("api_key", properties.getKey())
                        .build(asteroidId))
                .retrieve()
                .body(NasaNeoLookupResponse.class);
    }
}
