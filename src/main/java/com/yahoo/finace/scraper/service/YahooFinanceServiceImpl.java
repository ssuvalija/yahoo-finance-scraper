package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.dto.TickerDto;
import com.yahoo.finace.scraper.mapper.TickerMapper;
import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.repository.StockPriceRepository;
import com.yahoo.finace.scraper.repository.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class YahooFinanceServiceImpl implements YahooFinanceService {

    private final YahooScraperService yahooScraperService;
    private final TickerRepository tickerRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockPriceDownloader stockPriceDownloader;
    private final TickerMapper tickerMapper;
    private static final Logger logger = LoggerFactory.getLogger(YahooFinanceServiceImpl.class);


    @Autowired
    public YahooFinanceServiceImpl(
            YahooScraperService yahooScraperService,
            TickerRepository tickerRepository,
            StockPriceRepository stockPriceRepository,
            StockPriceDownloader stockPriceDownloader, TickerMapper tickerMapper) {
        this.yahooScraperService = yahooScraperService;
        this.tickerRepository = tickerRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.stockPriceDownloader = stockPriceDownloader;
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
                List<Ticker> missingTickers = yahooScraperService.fetchData(missingSymbols);
                saveTickers(missingTickers);
                tickersWithStockPrices.addAll(missingTickers);
                stockPriceDownloader.downloadAndMapStockPrices(missingTickers.get(0).getTickerSymbol(), LocalDate.now());
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

    @Transactional
    public void saveTickers(List<Ticker> tickers) {
        tickerRepository.saveAll(tickers);
    }
}
