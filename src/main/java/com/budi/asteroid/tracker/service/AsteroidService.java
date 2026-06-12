package com.budi.asteroid.tracker.service;

import com.budi.asteroid.tracker.client.NasaApiClient;
import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.dto.AsteroidResponse;
import com.budi.asteroid.tracker.validator.DateRangeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class AsteroidService {
    private static final int MAX_RESULT = 10;
    private final NasaApiClient nasaApiClient;
    private final DateRangeValidator dateRangeValidator;

    @Autowired
    public AsteroidService(NasaApiClient nasaApiClient, DateRangeValidator dateRangeValidator) {
        this.nasaApiClient = nasaApiClient;
        this.dateRangeValidator = dateRangeValidator;
    }

    public List<AsteroidResponse> findClosestAsteroids(LocalDate startDate,LocalDate endDate) {
        dateRangeValidator.validate(startDate, endDate);

        NasaNeoFeedResponse response = nasaApiClient.getNeoFeed(startDate,endDate);
        if (response.getNearEarthObjects() == null) {
            return List.of();
        }

        return response.getNearEarthObjects()
                .values()
                .stream()
                .flatMap(List::stream)
                .map(this::toResponse)
                .sorted(Comparator.comparing(AsteroidResponse::getMissDistanceKm))
                .limit(MAX_RESULT)
                .toList();
    }

    private AsteroidResponse toResponse(NasaNeoFeedResponse.NasaAsteroid asteroid) {
        NasaNeoFeedResponse.CloseApproachData closeApproachData = asteroid.getCloseApproachData().get(0);

        return AsteroidResponse.builder()
                .id(asteroid.getId())
                .name(asteroid.getName())
                .missDistanceKm(closeApproachData.getMissDistance().getKilometers())
                .estimatedDiameterMinMeters(asteroid.getEstimatedDiameter().getMeters().getMin())
                .estimatedDiameterMaxMeters(asteroid.getEstimatedDiameter().getMeters().getMax())
                .potentiallyHazardous(asteroid.getPotentiallyHazardous())
                .build();
    }

}
