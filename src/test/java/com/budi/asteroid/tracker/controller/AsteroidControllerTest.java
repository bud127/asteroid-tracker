package com.budi.asteroid.tracker.controller;

import com.budi.asteroid.tracker.dto.AsteroidResponse;
import com.budi.asteroid.tracker.exception.AsteroidNotFoundException;
import com.budi.asteroid.tracker.exception.NasaApiException;
import com.budi.asteroid.tracker.service.AsteroidService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsteroidController.class)
class AsteroidControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AsteroidService asteroidService;

    @Test
    void shouldReturnClosestAsteroids() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 7);
        when(asteroidService.findClosestAsteroids(startDate, endDate))
                .thenReturn(List.of(
                        AsteroidResponse.builder()
                                .id("1")
                                .name("Asteroid A")
                                .missDistanceKm(BigDecimal.valueOf(100000))
                                .estimatedDiameterMinMeters(BigDecimal.TEN)
                                .estimatedDiameterMaxMeters(BigDecimal.valueOf(20))
                                .potentiallyHazardous(false)
                                .build()
                ));

        mockMvc.perform(get("/api/asteroids")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-07"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Asteroid A"))
                .andExpect(jsonPath("$[0].missDistanceKm").value(100000))
                .andExpect(jsonPath("$[0].estimatedDiameterMinMeters").value(10))
                .andExpect(jsonPath("$[0].estimatedDiameterMaxMeters").value(20))
                .andExpect(jsonPath("$[0].potentiallyHazous").doesNotExist())
                .andExpect(jsonPath("$[0].potentiallyHazardous").value(false));
    }

    @Test
    void shouldReturnBadRequestWhenDateFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/api/asteroids")
                        .param("startDate", "invalid-date")
                        .param("endDate", "2025-01-07"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenStartDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/asteroids")
                        .param("endDate", "2025-01-07"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadGatewayWhenNasaApiFails() throws Exception {
        when(asteroidService.findClosestAsteroids(any(), any()))
                .thenThrow(new NasaApiException("Failed to retrieve data from NASA API"));

        mockMvc.perform(get("/api/asteroids")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-07"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void shouldReturnAsteroidDetail() throws Exception {
        when(asteroidService.getAsteroidDetail("123"))
                .thenReturn(AsteroidResponse.builder()
                        .id("123")
                        .name("Asteroid Detail")
                        .nasaJplUrl("https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=123")
                        .estimatedDiameterMinMeters(BigDecimal.TEN)
                        .estimatedDiameterMaxMeters(BigDecimal.valueOf(20))
                        .potentiallyHazardous(false)
                        .build());

        mockMvc.perform(get("/api/asteroids/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Asteroid Detail"))
                .andExpect(jsonPath("$.nasaJplUrl").value("https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=123"));
    }


    @Test
    void shouldReturnNotFoundWhenAsteroidDoesNotExist() throws Exception {
        when(asteroidService.getAsteroidDetail("not-found"))
                .thenThrow(new AsteroidNotFoundException("Asteroid not found: not-found"));

        mockMvc.perform(get("/api/asteroids/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Asteroid not found: not-found"));
    }
}
