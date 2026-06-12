package com.budi.asteroid.tracker.controller;

import com.budi.asteroid.tracker.dto.AsteroidResponse;
import com.budi.asteroid.tracker.service.AsteroidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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
    public List<AsteroidResponse> getAsteroids(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return asteroidService.findClosestAsteroids(startDate, endDate);
    }

    @GetMapping("/{id}")
    public AsteroidResponse getAsteroidDetail(@PathVariable String id) {
        return asteroidService.getAsteroidDetail(id);
    }

}