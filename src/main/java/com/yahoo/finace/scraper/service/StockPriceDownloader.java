package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.StockPrice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface StockPriceDownloader {
    List<StockPrice> downloadAndMapStockPrices(String tickerSymbol, LocalDate currentDate) throws IOException;
}
