package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.dto.TickerDto;
import com.yahoo.finace.scraper.mapper.TickerMapper;
import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.repository.StockPriceRepository;
import com.yahoo.finace.scraper.repository.TickerRepository;
import com.yahoo.finace.scraper.utils.YahooScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class YahooFinanceServiceImpl implements YahooFinanceService {

    private final YahooScraper yahooScraper;
    private final TickerRepository tickerRepository;
    private final StockPriceRepository stockPriceRepository;
    private final TickerStockPriceService tickerStockPriceService;
    private final TickerMapper tickerMapper;
    private static final Logger logger = LoggerFactory.getLogger(YahooFinanceServiceImpl.class);


    @Autowired
    public YahooFinanceServiceImpl(
            YahooScraper yahooScraper,
            TickerRepository tickerRepository,
            StockPriceRepository stockPriceRepository,
            TickerStockPriceService tickerStockPriceService,
            TickerMapper tickerMapper) {
        this.yahooScraper = yahooScraper;
        this.tickerRepository = tickerRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.tickerStockPriceService = tickerStockPriceService;
        this.tickerMapper = tickerMapper;
    }

    // Check if data exists in the database, if not fetch from Yahoo Finance
    // Return the fetched financial data as a list of TickerDto
    @Override
    public List<TickerDto> getTickersAndStockPrices(List<String> tickers, LocalDate date) {
        Set<Ticker> tickersWithStockPrices = tickerRepository.getTickersWithStockPrices(tickers, date);

        List<String> missingSymbols = tickers.stream()
                .filter(tickerSymbol ->
                        tickersWithStockPrices.stream()
                                .noneMatch(ticker -> ticker.getTickerSymbol().equals(tickerSymbol)))
                .collect(Collectors.toList());

        if (!missingSymbols.isEmpty()) {
            try {
                List<Ticker> missingTickers = yahooScraper.fetchData(missingSymbols);
                tickerStockPriceService.saveTickers(missingTickers);
                tickersWithStockPrices.addAll(missingTickers);
            } catch (IOException ex) {
                logger.error("Error occurred while trying to scrape data for tickers: " + missingSymbols);
            }
        }

        return tickersWithStockPrices.stream().map(tickerMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<String> getTrendingTickers() {
        // Placeholder logic: Fetch and return trending tickers from Yahoo Finance
        return List.of("AAPL", "GOOGL", "MSFT"); // Replace with actual trending tickers
    }

    @Override
    public TickerDto getLatestFinancialData(String ticker) {
        // Placeholder logic: Fetch latest financial data for the specified ticker from Yahoo Finance
        return new TickerDto(); // Replace with actual TickerDto
    }
}
