package com.yahoo.finace.scraper.controller;

import com.yahoo.finace.scraper.dto.TickerDto;
import com.yahoo.finace.scraper.service.YahooFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class YahooFinanceController {

    private final YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/financial-data")
    public List<TickerDto> getFinancialData(
            @RequestParam List<String> tickers,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
       return yahooFinanceService.getTickersAndStockPrices(tickers, date);
    }

    @GetMapping("/trending-tickers")
    public List<String> getTrendingTickers() {
        // Placeholder logic: Call the service to fetch trending tickers
        return yahooFinanceService.getTrendingTickers();
    }

    @GetMapping("/latest-financial-data/{ticker}")
    public TickerDto getLatestFinancialData(@PathVariable String ticker) {
        // Placeholder logic: Call the service to fetch the latest financial data
        return yahooFinanceService.getLatestFinancialData(ticker);
    }
}