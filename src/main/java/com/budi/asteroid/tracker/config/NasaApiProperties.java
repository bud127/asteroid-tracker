package com.budi.asteroid.tracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nasa.api")
public class NasaApiProperties {
    private String baseUrl;
    private String key;
    private int connectTimeout;
    private int readTimeout;
}
