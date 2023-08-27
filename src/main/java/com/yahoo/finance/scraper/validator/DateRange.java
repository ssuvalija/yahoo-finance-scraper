package com.yahoo.finance.scraper.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface DateRange {
    int maxYearsAgo() default 1;
    String message() default "Date must be within the last {maxYearsAgo} years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
