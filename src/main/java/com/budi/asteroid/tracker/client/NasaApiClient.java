package com.budi.asteroid.tracker.client;

import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.client.dto.NasaNeoLookupResponse;

import java.time.LocalDate;

public interface NasaApiClient {

    NasaNeoFeedResponse getNeoFeed(LocalDate startDate,LocalDate endDate);
    NasaNeoLookupResponse getNeoLookup(String asteroidId);
}
