package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.dto.TickerResponseDto;
import com.yahoo.finace.scraper.mapper.TickerMapper;
import com.yahoo.finace.scraper.model.StockPrice;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Transactional
    public List<TickerResponseDto> getTickersAndStockPrices(List<String> tickers, LocalDate date) {
        Set<Ticker> tickersWithStockPrices = tickerRepository.getTickersWithStockPrices(tickers, date);

        //List of tickers that we are missing completely in DB
        List<String> missingSymbols = tickers.stream()
                .filter(tickerSymbol ->
                        tickersWithStockPrices.stream()
                                .noneMatch(ticker -> ticker.getTickerSymbol().equals(tickerSymbol)))
                .collect(Collectors.toList());

        //List of tickers that exists in DB, but we are missing data for the passed date
        List<Ticker> missingStockPricesForTickers = tickerRepository.getTickers(missingSymbols);

        //remove tickers that exist in DB
        missingSymbols.removeIf(ms -> !missingStockPricesForTickers.stream().filter(t -> t.getTickerSymbol().equals(ms)).collect(Collectors.toList()).isEmpty());

        //Get missing stock data for existing tickers
        for (Ticker ticker : missingStockPricesForTickers) {
            if (ticker.getStockPrices() != null && !ticker.getStockPrices().isEmpty()) {
                LocalDate latestRecordDate = ticker.getStockPrices().stream()
                        .map(StockPrice::getDate)
                        .max(Comparator.naturalOrder())
                        .get();

                //If passed date is before than the date of the latest record in database
                //we have a gap, that probably means the date falls on weekend or holiday
                //In that case we can return empty list that can be interpreted on the frontend
                //as no data for the selected date
                if (date.isBefore(latestRecordDate)) {
                    tickersWithStockPrices.add(ticker);
                } else if (date.isAfter(latestRecordDate)) {
                    //If passed date is after the date of the latest record in database
                    //we should calculate the gap and download data between those two days
                    try {
                        Ticker updatedTicker = yahooScraperService.fetchData(ticker.getTickerSymbol());
                        updateTicker(ticker, updatedTicker);

                        List<StockPrice> stockPrices = stockPriceDownloader.downloadAndMapStockPrices(ticker, latestRecordDate, date.plusDays(1));
                        ticker.setStockPrices(updateStockPrices(ticker.getStockPrices(), stockPrices));

                    } catch (IOException ex) {
                        logger.error("Error occurred while trying to scrape data for tickers: " + missingSymbols);
                    }
                }
            }
        }

        if (!missingStockPricesForTickers.isEmpty()) {
            saveTickers(missingStockPricesForTickers);
        }
        for (Ticker ticker: missingStockPricesForTickers) {
            ticker.setStockPrices(ticker.getStockPrices().stream().filter(sp -> sp.getDate().equals(date)).collect(Collectors.toList()));
        }
        tickersWithStockPrices.addAll(missingStockPricesForTickers);


        //Tickers data is missing, fetch the ticker and historical data
        if (!missingSymbols.isEmpty()) {
            try {
                List<Ticker> missingTickers = yahooScraperService.fetchData(missingSymbols);

                for (Ticker ticker : missingTickers) {
                    List<StockPrice> stockPrices = stockPriceDownloader.downloadAndMapStockPrices(ticker, LocalDate.now());

                    //delete today's data from historical records to avoid duplicates
                    stockPrices.removeIf(stockPrice -> stockPrice.getDate().isEqual(LocalDate.now()));

                    if (stockPrices != null && !stockPrices.isEmpty()) {
                        if (ticker.getStockPrices() == null) {
                            ticker.setStockPrices(new ArrayList<>());
                        }

                        ticker.setStockPrices(
                                Stream.concat(ticker.getStockPrices().stream(), stockPrices.stream())
                                        .sorted(Comparator.comparing(StockPrice::getDate).reversed())
                                        .collect(Collectors.toList()));
                    }
                }
                saveTickers(missingTickers);

                for (Ticker ticker: missingTickers) {
                    ticker.setStockPrices(ticker.getStockPrices().stream().filter(sp -> sp.getDate().equals(date)).collect(Collectors.toList()));
                }
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
        return List.of("AAPL", "GOOGL", "MSFT", "BA"); // Replace with actual trending tickers
    }

    @Override
    public TickerResponseDto getLatestFinancialData(String tickerSymbol) throws IOException {
        // Placeholder logic: Fetch the latest financial data for the specified ticker from Yahoo Finance
        Ticker ticker = yahooScraperService.fetchData(tickerSymbol);
        return tickerMapper.toDto(ticker);
    }

    @Transactional
    public void saveTickers(List<Ticker> tickers) {
        tickerRepository.saveAll(tickers);
    }

    @Transactional
    public void saveStockPrices(List<StockPrice> prices) {
        stockPriceRepository.saveAll(prices);
    }

    public Ticker updateTicker(Ticker oldTicker, Ticker newTicker) {
        oldTicker.setCompanyName(newTicker.getCompanyName());
        oldTicker.setCountry(newTicker.getCountry());
        oldTicker.setCity(newTicker.getCity());
        oldTicker.setState(newTicker.getState());
        oldTicker.setYearFounded(newTicker.getYearFounded());
        oldTicker.setNumberOfEmployees(newTicker.getNumberOfEmployees());
        oldTicker.setMarketCap(newTicker.getMarketCap());
        List<StockPrice> updatedStockPrices = newTicker.getStockPrices();
        for (StockPrice sp : updatedStockPrices) {
            sp.setTicker(oldTicker);
        }
        oldTicker.setStockPrices(updateStockPrices(oldTicker.getStockPrices(), updatedStockPrices));
        return oldTicker;
    }

    private List<StockPrice> updateStockPrices(List<StockPrice> oldStockPrices, List<StockPrice> newStockPrices) {
        for (StockPrice newStockPrice: newStockPrices) {
            Optional<StockPrice> existing = oldStockPrices.stream().filter(sp -> sp.getDate().equals(newStockPrice.getDate())).findFirst();
            if (existing.isPresent()) {
                updateStockPrice(existing.get(), newStockPrice);
            } else {
                oldStockPrices.add(newStockPrice);
            }

            Collections.reverse(oldStockPrices);
        }
        return oldStockPrices;
    }
    private StockPrice updateStockPrice(StockPrice oldStockPrice, StockPrice newStockPrice) {
        oldStockPrice.setPreviousClosePrice(newStockPrice.getPreviousClosePrice());
        oldStockPrice.setOpenPrice(newStockPrice.getOpenPrice());
        oldStockPrice.setMarketOpen(newStockPrice.isMarketOpen());
        return oldStockPrice;
    }
}
