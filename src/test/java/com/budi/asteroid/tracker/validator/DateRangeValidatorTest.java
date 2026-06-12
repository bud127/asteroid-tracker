package com.budi.asteroid.tracker.validator;

import com.budi.asteroid.tracker.exception.InvalidDateRangeException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeValidatorTest {

    private final DateRangeValidator validator = new DateRangeValidator();

    @Test
    void shouldNotThrowExceptionWhenDateRangeIsValid() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 7);
        assertThatNoException().isThrownBy(() -> validator.validate(startDate, endDate));
    }

    @Test
    void shouldThrowExceptionWhenDateRangeIsMoreThanSevenDays() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 9);
        assertThatThrownBy(() -> validator.validate(startDate, endDate))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessage("Date range must not be more than 7 days");
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        LocalDate startDate = LocalDate.of(2025, 1, 10);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        assertThatThrownBy(() -> validator.validate(startDate, endDate))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessage("Start date must not be after end date");
    }

}
