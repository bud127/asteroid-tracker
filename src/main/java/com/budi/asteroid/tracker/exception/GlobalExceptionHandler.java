package com.budi.asteroid.tracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateRange(InvalidDateRangeException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
        LOGGER.warn("Invalid request received", exception);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameter");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        LOGGER.error("Unexpected application error", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(),
                status.value(), status.getReasonPhrase(),message);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NasaApiException.class)
    public ResponseEntity<ErrorResponse> handleNasaApiException(NasaApiException exception) {
        LOGGER.error("NASA API request failed", exception);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(AsteroidNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAsteroidNotFound(AsteroidNotFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

}
