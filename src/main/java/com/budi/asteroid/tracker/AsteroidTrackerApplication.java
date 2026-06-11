package com.budi.asteroid.tracker;

import com.budi.asteroid.tracker.config.NasaApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({NasaApiProperties.class})
public class AsteroidTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsteroidTrackerApplication.class, args);
	}

}
