package com.budi.asteroid.tracker.validator;

import com.budi.asteroid.tracker.exception.InvalidDateRangeException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DateRangeValidator {

    private static final long MAX_RANGE_DAYS = 7;

    public void validate(LocalDate startDate, LocalDate endDate
    ){
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must not be after end date");
        }
        long days= ChronoUnit.DAYS.between(startDate, endDate);
        if (days > MAX_RANGE_DAYS-1) {
            throw new InvalidDateRangeException("Date range must not be more than " + MAX_RANGE_DAYS + " days");
        }
    }
}
