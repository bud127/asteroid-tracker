package com.budi.asteroid.tracker.service;

import com.budi.asteroid.tracker.dto.AsteroidResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsteroidService {

    public List<AsteroidResponse> findClosestAsteroids(LocalDate startDate,
                                                       LocalDate endDate) {
        return List.of();
    }

}
