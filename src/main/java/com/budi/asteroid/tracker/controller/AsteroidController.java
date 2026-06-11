package com.budi.asteroid.tracker.controller;

import com.budi.asteroid.tracker.dto.AsteroidResponse;
import com.budi.asteroid.tracker.service.AsteroidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asteroids")
public class AsteroidController {

    private final AsteroidService asteroidService;

    @Autowired
    public AsteroidController(AsteroidService asteroidService) {
        this.asteroidService = asteroidService;
    }

    @GetMapping
    public List<AsteroidResponse> getAsteroids(@RequestParam LocalDate startDate,
                                               @RequestParam LocalDate endDate) {
        return asteroidService.findClosestAsteroids(startDate, endDate);
    }

}