package com.budi.asteroid.tracker.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NasaNeoLookupResponse {

    private String id;

    private String name;

    @JsonProperty("nasa_jpl_url")
    private String nasaJplUrl;

    @JsonProperty("estimated_diameter")
    private EstimatedDiameter estimatedDiameter;

    @JsonProperty("is_potentially_hazardous_asteroid")
    private Boolean potentiallyHazardous;

    @Data
    public static class EstimatedDiameter {

        private DiameterInMeters meters;
    }

    @Data
    public static class DiameterInMeters {

        @JsonProperty("estimated_diameter_min")
        private BigDecimal min;

        @JsonProperty("estimated_diameter_max")
        private BigDecimal max;
    }
}
