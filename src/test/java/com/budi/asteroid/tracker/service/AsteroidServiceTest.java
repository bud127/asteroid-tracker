package com.budi.asteroid.tracker.service;

import com.budi.asteroid.tracker.client.NasaApiClient;
import com.budi.asteroid.tracker.client.dto.NasaNeoFeedResponse;
import com.budi.asteroid.tracker.dto.AsteroidResponse;
import com.budi.asteroid.tracker.validator.DateRangeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AsteroidServiceTest {

    @Mock
    private NasaApiClient nasaApiClient;
    @Mock
    private DateRangeValidator dateRangeValidator;
    @InjectMocks
    private AsteroidService asteroidService;

    @Test
    void shouldReturnAsteroidSortByClosestDistance() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 2);
        when(nasaApiClient.getNeoFeed(startDate,endDate))
                .thenReturn(feedResponse(asteroid("1","Far Asteroid","900000"),
                        asteroid("2","Closest Asteroid","100000")));
        List<AsteroidResponse> result=asteroidService.findClosestAsteroids(startDate,endDate);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Closest Asteroid");
        assertThat(result.get(1).getName()).isEqualTo("Far Asteroid");
    }

    @Test
    void shouldReturnOnlyTopTenClosestAsteroids() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        when(nasaApiClient.getNeoFeed(startDate, endDate))
                .thenReturn(feedResponse(
                        asteroid("1", "Asteroid 1", "100000"),
                        asteroid("2", "Asteroid 2", "200000"),
                        asteroid("3", "Asteroid 3", "300000"),
                        asteroid("4", "Asteroid 4", "400000"),
                        asteroid("5", "Asteroid 5", "500000"),
                        asteroid("6", "Asteroid 6", "600000"),
                        asteroid("7", "Asteroid 7", "700000"),
                        asteroid("8", "Asteroid 8", "800000"),
                        asteroid("9", "Asteroid 9", "900000"),
                        asteroid("10", "Asteroid 10", "1000000"),
                        asteroid("11", "Asteroid 11", "1100000")
                ));
        List<AsteroidResponse> result = asteroidService.findClosestAsteroids(startDate,endDate);
        assertThat(result).hasSize(10);
        assertThat(result).extracting(AsteroidResponse::getName).doesNotContain("Asteroid 11");
    }

    private NasaNeoFeedResponse feedResponse(NasaNeoFeedResponse.NasaAsteroid... asteroids) {
        NasaNeoFeedResponse response = new NasaNeoFeedResponse();
        response.setNearEarthObjects(Map.of(LocalDate.of(2025, 1, 1), List.of(asteroids))
        );
        return response;
    }

    private NasaNeoFeedResponse.NasaAsteroid asteroid(String id,String name, String missDistanceKm) {
        NasaNeoFeedResponse.NasaAsteroid asteroid = new NasaNeoFeedResponse.NasaAsteroid();
        asteroid.setId(id);
        asteroid.setName(name);
        asteroid.setPotentiallyHazardous(false);
        NasaNeoFeedResponse.EstimatedDiameter estimatedDiameter = new NasaNeoFeedResponse.EstimatedDiameter();
        NasaNeoFeedResponse.DiameterInMeters meters = new NasaNeoFeedResponse.DiameterInMeters();
        meters.setMin(BigDecimal.TEN);
        meters.setMax(BigDecimal.valueOf(20));
        estimatedDiameter.setMeters(meters);
        asteroid.setEstimatedDiameter(estimatedDiameter);
        NasaNeoFeedResponse.CloseApproachData approachData = new NasaNeoFeedResponse.CloseApproachData();
        NasaNeoFeedResponse.MissDistance missDistance = new NasaNeoFeedResponse.MissDistance();
        missDistance.setKilometers(new BigDecimal(missDistanceKm));
        approachData.setMissDistance(missDistance);
        asteroid.setCloseApproachData(List.of(approachData));
        return asteroid;
    }
}
