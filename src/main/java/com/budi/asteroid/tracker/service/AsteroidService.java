package com.budi.asteroid.tracker.service;

import com.budi.asteroid.tracker.client.NasaApiClient;
import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.client.dto.NasaNeoLookupResponse;
import com.budi.asteroid.tracker.dto.AsteroidResponse;
import com.budi.asteroid.tracker.exception.NasaApiException;
import com.budi.asteroid.tracker.validator.DateRangeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        if (response==null||response.getNearEarthObjects() == null) {
            return List.of();
        }

        return response.getNearEarthObjects()
                .values()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .filter(this::hasRequiredData)
                .map(this::toResponse)
                .sorted(Comparator.comparing(AsteroidResponse::getMissDistanceKm))
                .limit(MAX_RESULT)
                .toList();
    }

    private boolean hasRequiredData(NasaNeoFeedResponse.NasaAsteroid asteroid) {
        return asteroid.getCloseApproachData() != null
                && asteroid.getCloseApproachData()
                .stream()
                .anyMatch(this::hasValidMissDistance)
                && asteroid.getEstimatedDiameter() != null
                && asteroid.getEstimatedDiameter().getMeters() != null
                && asteroid.getEstimatedDiameter().getMeters().getMin() != null
                && asteroid.getEstimatedDiameter().getMeters().getMax() != null;
    }

    private NasaNeoFeedResponse.CloseApproachData findClosestApproach(NasaNeoFeedResponse.NasaAsteroid asteroid) {
        return asteroid.getCloseApproachData()
                .stream()
                .filter(this::hasValidMissDistance)
                .min(Comparator.comparing(data -> data.getMissDistance().getKilometers()))
                .orElseThrow();
    }

    private boolean hasValidMissDistance(NasaNeoFeedResponse.CloseApproachData data) {
        return data!=null && data.getMissDistance() != null && data.getMissDistance().getKilometers() != null;

    }

    private AsteroidResponse toResponse(NasaNeoFeedResponse.NasaAsteroid asteroid) {
        NasaNeoFeedResponse.CloseApproachData closestApproach = findClosestApproach(asteroid);

        return AsteroidResponse.builder()
                .id(asteroid.getId())
                .name(asteroid.getName())
                .missDistanceKm(closestApproach.getMissDistance().getKilometers())
                .estimatedDiameterMinMeters(asteroid.getEstimatedDiameter().getMeters().getMin())
                .estimatedDiameterMaxMeters(asteroid.getEstimatedDiameter().getMeters().getMax())
                .potentiallyHazardous(asteroid.getPotentiallyHazardous())
                .build();
    }

    public AsteroidResponse getAsteroidDetail(String asteroidId) {
        NasaNeoLookupResponse lookup = nasaApiClient.getNeoLookup(asteroidId);
        if (!hasValidLookupData(lookup)) {
            throw new NasaApiException("Failed to retrieve asteroid detail from NASA API"
            );
        }
        return AsteroidResponse.builder()
                .id(lookup.getId())
                .name(lookup.getName())
                .nasaJplUrl(lookup.getNasaJplUrl())
                .estimatedDiameterMinMeters(lookup.getEstimatedDiameter().getMeters().getMin())
                .estimatedDiameterMaxMeters(lookup.getEstimatedDiameter().getMeters().getMax())
                .potentiallyHazardous(lookup.getPotentiallyHazardous())
                .build();

    }

    private boolean hasValidLookupData(NasaNeoLookupResponse lookup) {
        return lookup != null
                && lookup.getEstimatedDiameter() != null
                && lookup.getEstimatedDiameter().getMeters() != null
                && lookup.getEstimatedDiameter().getMeters().getMin() != null
                && lookup.getEstimatedDiameter().getMeters().getMax() != null;
    }

}
