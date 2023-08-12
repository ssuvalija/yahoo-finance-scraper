package com.yahoo.finace.scraper.service;
import com.yahoo.finace.scraper.dto.TickerResponseDto;
import com.yahoo.finace.scraper.model.Ticker;
import org.springframework.cglib.core.Local;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface YahooFinanceService {
    List<TickerResponseDto> getTickersAndStockPrices(List<String> tickers, LocalDate date);
    List<String> getTrendingTickers();
    TickerResponseDto getLatestFinancialData(String ticker) throws IOException;
    void saveTickers(List<Ticker> tickers);
    Ticker updateTicker(Ticker oldTicker, Ticker newTicker);
    void deleteOldStockPrices(LocalDate cutoffDate);
}