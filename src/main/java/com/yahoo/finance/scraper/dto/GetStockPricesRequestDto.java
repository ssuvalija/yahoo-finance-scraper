package com.yahoo.finance.scraper.dto;

import com.yahoo.finance.scraper.validator.DateRange;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStockPricesRequestDto {
    @NotNull(message = "Tickers list cannot be null")
    @NotEmpty(message = "Tickers list cannot be empty")
    private List<String> tickers;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date must be present or in the past")
    @DateRange(maxYearsAgo = 5, message = "Date must be within the last 5 years")
    private LocalDate date;

}
