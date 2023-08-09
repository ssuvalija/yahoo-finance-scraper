package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.Ticker;

import java.util.List;

public interface TickerStockPriceService {
    public void saveTickers(List<Ticker> tickers);
}
