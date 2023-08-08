package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;

public interface TickerStockPriceServiceInterface {
    public void saveTickerAndStockPrice(Ticker ticker, StockPrice stockPrice);
}
