package com.yahoo.finace.scraper.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TickerResponseDto {
    private Long tickerId;
    private String tickerSymbol;
    private String companyName;
    private String marketCap;
    private int yearFounded;
    private int numberOfEmployees;
    private String city;
    private String state;
    private String country;
    private List<StockPriceResponseDto> stockPriceDtoList;
    private LocalDateTime lastUpdated;
}
