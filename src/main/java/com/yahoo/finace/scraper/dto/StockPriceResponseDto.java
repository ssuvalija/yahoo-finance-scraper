package com.yahoo.finace.scraper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceResponseDto {
    private Long stockPriceId;
    private LocalDate date;
    private BigDecimal previousClosePrice;
    private BigDecimal openPrice;
    private boolean isMarketOpen;
    private LocalDateTime lastUpdated;
}
