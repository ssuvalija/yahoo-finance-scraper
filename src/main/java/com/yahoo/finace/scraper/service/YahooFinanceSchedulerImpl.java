package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.repository.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
        List<Ticker> tickers = tickerRepository.findAll();
        for (Ticker ticker : tickers) {
            try {
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
}
