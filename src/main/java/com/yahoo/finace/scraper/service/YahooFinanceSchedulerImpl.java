package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.repository.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class YahooFinanceSchedulerImpl implements YahooFinanceScheduler {
    private final YahooFinanceService yahooFinanceService;
    private final TickerRepository tickerRepository;
    private final YahooScraperService yahooScraperService;
    private static final Logger logger = LoggerFactory.getLogger(YahooFinanceScheduler.class);


    @Autowired
    public YahooFinanceSchedulerImpl(YahooFinanceService yahooFinanceService, TickerRepository tickerRepository, YahooScraperService yahooScraperService) {
        this.yahooFinanceService = yahooFinanceService;
        this.tickerRepository = tickerRepository;
        this.yahooScraperService = yahooScraperService;
    }

    @Override
    @Transactional
    @Scheduled(cron = "* */15 * * * *") // Run every 15 minutes
    public void fetchLatestDataForTickers() {
        logger.info("Running scheduled task to fetch latest data from Yahoo Finance");
        List<Ticker> tickers = tickerRepository.findAll();
        for (Ticker ticker : tickers) {
            try {
                logger.info("Fetching data for {}", ticker.getTickerSymbol());
                Ticker updatedTicker = yahooScraperService.fetchData(ticker.getTickerSymbol());
                yahooFinanceService.updateTicker(ticker, updatedTicker);
            } catch (IOException ex) {
                logger.error("Error occurred while fetching ticker data for symbol: " +
                        ticker.getTickerSymbol()
                        + ". Error: " + ex.getLocalizedMessage());
            }
        }
        tickerRepository.saveAll(tickers);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void scheduledStockPriceCleanup() {
        //delete stock prices older than 5 years
        logger.info("Running scheduled task to delete old stock prices data.");
        LocalDate cutoffDate = LocalDate.now().minusYears(5);
        yahooFinanceService.deleteOldStockPrices(cutoffDate);
    }
}
