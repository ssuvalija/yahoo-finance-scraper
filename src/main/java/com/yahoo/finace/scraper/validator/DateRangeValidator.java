package com.yahoo.finace.scraper.validator;

import com.yahoo.finace.scraper.dto.GetStockPricesRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, LocalDate> {

    private int maxYearsAgo;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        maxYearsAgo = constraintAnnotation.maxYearsAgo();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        LocalDate currentDate = LocalDate.now();
        LocalDate minDate = currentDate.minusYears(maxYearsAgo);

        return date != null && !date.isAfter(currentDate) && !date.isBefore(minDate);
    }
}