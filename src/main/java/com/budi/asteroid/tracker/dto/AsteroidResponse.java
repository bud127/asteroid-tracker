package com.budi.asteroid.tracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsteroidResponse {
    private String id;
    private String name;
    private BigDecimal missDistanceKm;
    private BigDecimal estimatedDiameterMinMeters;
    private BigDecimal estimatedDiameterMaxMeters;
    private Boolean potentiallyHazardous;
    private String nasaJplUrl;
}
