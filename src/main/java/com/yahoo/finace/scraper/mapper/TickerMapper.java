package com.yahoo.finace.scraper.mapper;

import com.yahoo.finace.scraper.dto.StockPriceDto;
import com.yahoo.finace.scraper.dto.TickerDto;
import com.yahoo.finace.scraper.model.Ticker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TickerMapper {

    private final StockPriceMapper stockPriceMapper;

    public TickerMapper(StockPriceMapper stockPriceMapper) {
        this.stockPriceMapper = stockPriceMapper;
    }

    public TickerDto toDto(Ticker ticker) {
        TickerDto dto = new TickerDto();
        dto.setTickerId(ticker.getTickerId());
        dto.setTickerSymbol(ticker.getTickerSymbol());
        dto.setCompanyName(ticker.getCompanyName());
        dto.setMarketCap(ticker.getMarketCap());
        dto.setYearFounded(ticker.getYearFounded());
        dto.setCity(ticker.getCity());
        dto.setState(ticker.getState());
        dto.setNumberOfEmployees(ticker.getNumberOfEmployees());
        List<StockPriceDto> stockPriceDtoList = ticker.getStockPrices().stream()
                .map(stockPriceMapper::toDto)
                .collect(Collectors.toList());
        dto.setStockPriceDtoList(stockPriceDtoList);
        return dto;
    }

    public Ticker toEntity(TickerDto dto) {
        Ticker ticker = new Ticker();
        ticker.setTickerId(dto.getTickerId());
        ticker.setTickerSymbol(dto.getTickerSymbol());
        ticker.setCompanyName(dto.getCompanyName());
        ticker.setMarketCap(dto.getMarketCap());
        ticker.setYearFounded(dto.getYearFounded());
        ticker.setCity(dto.getCity());
        ticker.setState(dto.getState());
        ticker.setNumberOfEmployees(dto.getNumberOfEmployees());
        return ticker;
    }
}
