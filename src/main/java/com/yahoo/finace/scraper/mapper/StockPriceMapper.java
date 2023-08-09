package com.yahoo.finace.scraper.mapper;

import com.yahoo.finace.scraper.dto.StockPriceDto;
import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.stereotype.Component;

@Component
public class StockPriceMapper {

    public StockPriceDto toDto(StockPrice stockPrice) {
        StockPriceDto dto = new StockPriceDto();
        dto.setStockPriceId(stockPrice.getStockPriceId());
        dto.setDateTime(stockPrice.getDateTime());
        dto.setPreviousClosePrice(stockPrice.getPreviousClosePrice());
        dto.setOpenPrice(stockPrice.getOpenPrice());
        return dto;
    }

    public StockPrice toEntity(StockPriceDto dto) {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setStockPriceId(dto.getStockPriceId());
        stockPrice.setDateTime(dto.getDateTime());
        stockPrice.setPreviousClosePrice(dto.getPreviousClosePrice());
        stockPrice.setOpenPrice(dto.getOpenPrice());
        return stockPrice;
    }
}