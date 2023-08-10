package com.yahoo.finace.scraper.dto;

import com.yahoo.finace.scraper.model.StockPrice;
import jakarta.persistence.Column;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TickerDto {
    private Long tickerId;
    private String tickerSymbol;
    private String companyName;
    private String marketCap;
    private int yearFounded;
    private int numberOfEmployees;
    private String city;
    private String state;
    private String country;
    private List<StockPriceDto> stockPriceDtoList;
}
