package com.yahoo.finace.scraper.service;
import com.yahoo.finace.scraper.dto.TickerDto;
import java.time.LocalDate;
import java.util.List;

public interface YahooFinanceService {
    List<TickerDto> getFinancialData(List<String> tickers, LocalDate date);
    List<String> getTrendingTickers();
    TickerDto getLatestFinancialData(String ticker);
}