package com.yahoo.finace.scraper.mapper;

import com.yahoo.finace.scraper.dto.StockPriceDto;
import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.stereotype.Component;

@Component
public class StockPriceMapper {

    public StockPriceDto toDto(StockPrice stockPrice) {
        StockPriceDto dto = new StockPriceDto();
        dto.setStockPriceId(stockPrice.getId());
        dto.setDate(stockPrice.getDate());
        dto.setPreviousClosePrice(stockPrice.getPreviousClosePrice());
        dto.setOpenPrice(stockPrice.getOpenPrice());
        dto.setMarketOpen(stockPrice.isMarketOpen());
        dto.setLastUpdated(stockPrice.getUpdatedAt());
        return dto;
    }

    public StockPrice toEntity(StockPriceDto dto) {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setId(dto.getStockPriceId());
        stockPrice.setDate(dto.getDate());
        stockPrice.setPreviousClosePrice(dto.getPreviousClosePrice());
        stockPrice.setOpenPrice(dto.getOpenPrice());
        stockPrice.setMarketOpen(dto.isMarketOpen());
        return stockPrice;
    }
}