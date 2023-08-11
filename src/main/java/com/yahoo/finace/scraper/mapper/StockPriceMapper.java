package com.yahoo.finace.scraper.mapper;

import com.yahoo.finace.scraper.dto.StockPriceResponseDto;
import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.stereotype.Component;

@Component
public class StockPriceMapper {

    public StockPriceResponseDto toDto(StockPrice stockPrice) {
        StockPriceResponseDto dto = new StockPriceResponseDto();
        dto.setStockPriceId(stockPrice.getId());
        dto.setDate(stockPrice.getDate());
        dto.setPreviousClosePrice(stockPrice.getPreviousClosePrice());
        dto.setOpenPrice(stockPrice.getOpenPrice());
        dto.setMarketOpen(stockPrice.isMarketOpen());
        dto.setLastUpdated(stockPrice.getUpdatedAt());
        return dto;
    }

    public StockPrice toEntity(StockPriceResponseDto dto) {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setId(dto.getStockPriceId());
        stockPrice.setDate(dto.getDate());
        stockPrice.setPreviousClosePrice(dto.getPreviousClosePrice());
        stockPrice.setOpenPrice(dto.getOpenPrice());
        stockPrice.setMarketOpen(dto.isMarketOpen());
        return stockPrice;
    }
}