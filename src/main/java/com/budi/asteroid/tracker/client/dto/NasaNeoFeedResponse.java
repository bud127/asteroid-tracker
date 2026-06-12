package com.budi.asteroid.tracker.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class NasaNeoFeedResponse {

    @JsonProperty("near_earth_objects")
    private Map<LocalDate, List<NasaAsteroid>> nearEarthObjects;

    @Data
    public static class NasaAsteroid {

        private String id;

        private String name;

        @JsonProperty("estimated_diameter")
        private EstimatedDiameter estimatedDiameter;

        @JsonProperty("is_potentially_hazardous_asteroid")
        private Boolean potentiallyHazardous;

        @JsonProperty("close_approach_data")
        private List<CloseApproachData> closeApproachData;
    }

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

    @Data
    public static class CloseApproachData {

        @JsonProperty("close_approach_date")
        private LocalDate closeApproachDate;

        @JsonProperty("miss_distance")
        private MissDistance missDistance;
    }

    @Data
    public static class MissDistance {

        @JsonProperty("kilometers")
        private BigDecimal kilometers;
    }
}