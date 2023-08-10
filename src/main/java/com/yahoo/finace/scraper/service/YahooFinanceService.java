package com.yahoo.finace.scraper.service;
import com.yahoo.finace.scraper.dto.TickerDto;
import com.yahoo.finace.scraper.model.Ticker;

import java.time.LocalDate;
import java.util.List;

public interface YahooFinanceService {
    List<TickerDto> getTickersAndStockPrices(List<String> tickers, LocalDate date);
    List<String> getTrendingTickers();
    TickerDto getLatestFinancialData(String ticker);
    void saveTickers(List<Ticker> tickers);

}