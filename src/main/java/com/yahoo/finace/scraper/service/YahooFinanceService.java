package com.yahoo.finace.scraper.service;
import com.yahoo.finace.scraper.dto.TickerResponseDto;
import com.yahoo.finace.scraper.model.Ticker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface YahooFinanceService {
    List<TickerResponseDto> getTickersAndStockPrices(List<String> tickers, LocalDate date);
    List<String> getTrendingTickers();
    TickerResponseDto getLatestFinancialData(String ticker) throws IOException;
    void saveTickers(List<Ticker> tickers);

}