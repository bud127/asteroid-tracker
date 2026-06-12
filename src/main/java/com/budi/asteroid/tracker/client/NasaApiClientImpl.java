package com.budi.asteroid.tracker.client;


import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.client.dto.NasaNeoLookupResponse;
import com.budi.asteroid.tracker.config.NasaApiProperties;
import com.budi.asteroid.tracker.exception.AsteroidNotFoundException;
import com.budi.asteroid.tracker.exception.NasaApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;

@Component
public class NasaApiClientImpl implements NasaApiClient {

    private final RestClient restClient;
    private final NasaApiProperties properties;

    @Autowired
    public NasaApiClientImpl(NasaApiProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public NasaNeoFeedResponse getNeoFeed(LocalDate startDate,LocalDate endDate) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/neo/rest/v1/feed")
                            .queryParam("start_date", startDate)
                            .queryParam("end_date", endDate)
                            .queryParam("api_key", properties.getKey())
                            .build())
                    .retrieve()
                    .body(NasaNeoFeedResponse.class);
        }catch (RestClientException e) {
            throw new NasaApiException("Failed to retrieve data from NASA API",e);
        }
    }

    @Override
    public NasaNeoLookupResponse getNeoLookup(String asteroidId) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/neo/rest/v1/neo/{asteroidId}")
                            .queryParam("api_key", properties.getKey())
                            .build(asteroidId))
                    .retrieve()
                    .body(NasaNeoLookupResponse.class);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new AsteroidNotFoundException("Asteroid not found: " + asteroidId);
        } catch (RestClientException exception) {
            throw new NasaApiException("Failed to retrieve data from NASA API", exception);
        }
    }
}
