package com.budi.asteroid.tracker.exception;

public class AsteroidNotFoundException extends RuntimeException {

    public AsteroidNotFoundException(String message) {
        super(message);
    }
}
