package com.yahoo.finace.scraper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceDto {
    private Long stockPriceId;
    private LocalDateTime dateTime;
    private BigDecimal previousClosePrice;
    private BigDecimal openPrice;
}
