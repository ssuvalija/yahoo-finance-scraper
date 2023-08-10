package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface StockPriceDownloader {
    List<StockPrice> downloadAndMapStockPrices(Ticker ticker, LocalDate startDate, LocalDate endDate) throws IOException;
    List<StockPrice> downloadAndMapStockPrices(Ticker ticker, LocalDate startDate) throws IOException;
}
