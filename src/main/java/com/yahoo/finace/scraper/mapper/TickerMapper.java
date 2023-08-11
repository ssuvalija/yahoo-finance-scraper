package com.yahoo.finace.scraper.mapper;

import com.yahoo.finace.scraper.dto.StockPriceResponseDto;
import com.yahoo.finace.scraper.dto.TickerResponseDto;
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

    public TickerResponseDto toDto(Ticker ticker) {
        TickerResponseDto dto = new TickerResponseDto();
        dto.setTickerId(ticker.getId());
        dto.setTickerSymbol(ticker.getTickerSymbol());
        dto.setCompanyName(ticker.getCompanyName());
        dto.setMarketCap(ticker.getMarketCap());
        dto.setYearFounded(ticker.getYearFounded());
        dto.setCity(ticker.getCity());
        dto.setState(ticker.getState());
        dto.setCountry(ticker.getCountry());
        dto.setNumberOfEmployees(ticker.getNumberOfEmployees());
        dto.setLastUpdated(ticker.getUpdatedAt());
        List<StockPriceResponseDto> stockPriceDtoList = ticker.getStockPrices().stream()
                .map(stockPriceMapper::toDto)
                .collect(Collectors.toList());
        dto.setStockPriceDtoList(stockPriceDtoList);
        return dto;
    }

    public Ticker toEntity(TickerResponseDto dto) {
        Ticker ticker = new Ticker();
        ticker.setId(dto.getTickerId());
        ticker.setTickerSymbol(dto.getTickerSymbol());
        ticker.setCompanyName(dto.getCompanyName());
        ticker.setMarketCap(dto.getMarketCap());
        ticker.setYearFounded(dto.getYearFounded());
        ticker.setCity(dto.getCity());
        ticker.setState(dto.getState());
        ticker.setCountry(dto.getCountry());
        ticker.setNumberOfEmployees(dto.getNumberOfEmployees());
        return ticker;
    }
}
