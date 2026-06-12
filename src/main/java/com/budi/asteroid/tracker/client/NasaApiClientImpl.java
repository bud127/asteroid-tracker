package com.budi.asteroid.tracker.client;


import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.client.dto.NasaNeoLookupResponse;
import com.budi.asteroid.tracker.config.NasaApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Component
public class NasaApiClientImpl implements NasaApiClient {

    private final RestClient restClient;
    private final NasaApiProperties properties;

    public NasaApiClientImpl(
            NasaApiProperties properties
    ) {
        this.properties = properties;

        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Override
    public NasaNeoFeedResponse getNeoFeed(
            LocalDate startDate,
            LocalDate endDate
    ) {

        throw new UnsupportedOperationException(
                "Not implemented yet"
        );
    }

    @Override
    public NasaNeoLookupResponse getNeoLookup(
            String asteroidId
    ) {

        throw new UnsupportedOperationException(
                "Not implemented yet"
        );
    }
}
