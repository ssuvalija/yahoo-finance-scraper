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
import java.util.function.Function;
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
    public List<TickerResponseDto> getTickersAndStockPrices(List<String> tickerSymbols, LocalDate date) {
        Set<Ticker> tickersWithStockPrices = tickerRepository.getTickersWithStockPrices(tickerSymbols, date);
        List<TickerResponseDto> result = tickersWithStockPrices.stream()
                .map(tickerMapper::toDto).collect(Collectors.toList());

        //List of tickerSymbols that we are missing completely in DB
        List<String> missingTickersSymbols = getSymbolsForMissingTickers(tickerSymbols, tickersWithStockPrices);

        //List of tickerSymbols that exists in DB, but we are missing data for the passed date
        List<Ticker> missingStockPricesForTickers = tickerRepository.getTickers(missingTickersSymbols);

        //remove tickerSymbols from missingTickersSymbols list that exist in DB
        removeTickerSymbols(missingTickersSymbols, missingStockPricesForTickers);

        //Get missing stock data for existing tickerSymbols
        fetchMissingStockPricesDataFromYahoo(date, result, missingTickersSymbols, missingStockPricesForTickers);

        //Tickers data is missing, fetch the ticker and historical data
        fetchMissingTickersDataFromYahoo(result, missingTickersSymbols);

        //In case when we are for the first time fetching historical stock prices
        //we will have multiple stock prices in result list, so we need to filter
        //stock prices for the given date
        filterStockPricesForDate(date, result);

        return result;
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

    @Override
    @Transactional
    public void deleteOldStockPrices(LocalDate cutoffDate) {
        stockPriceRepository.deleteOlderThan(cutoffDate);
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

    private static void filterStockPricesForDate(LocalDate date, List<TickerResponseDto> result) {
        for (TickerResponseDto responseDto: result) {
            responseDto.setStockPriceDtoList(
                    responseDto.getStockPriceDtoList().stream().
                            filter(sp -> sp.getDate().equals(date)).collect(Collectors.toList()));
        }
    }

    private void fetchMissingTickersDataFromYahoo(List<TickerResponseDto> result, List<String> missingTickersSymbols) {
        if (!missingTickersSymbols.isEmpty()) {
            try {
                List<Ticker> missingTickers = yahooScraperService.fetchData(missingTickersSymbols);

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

                result.addAll(missingTickers.stream().map(tickerMapper::toDto).collect(Collectors.toList()));

            } catch (IOException ex) {
                logger.error("Error occurred while trying to scrape data for tickerSymbols: " + missingTickersSymbols);
            }
        }
    }

    private void fetchMissingStockPricesDataFromYahoo(LocalDate date, List<TickerResponseDto> result, List<String> missingTickersSymbols, List<Ticker> missingStockPricesForTickers) {
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
                if (date.isAfter(latestRecordDate)) {
                    //If passed date is after the date of the latest record in database
                    //we should calculate the gap and download data between those two days
                    try {
                        Ticker updatedTicker = yahooScraperService.fetchData(ticker.getTickerSymbol());
                        updateTicker(ticker, updatedTicker);

                        List<StockPrice> stockPrices = stockPriceDownloader.downloadAndMapStockPrices(ticker, latestRecordDate, date.plusDays(1));
                        ticker.setStockPrices(updateStockPrices(ticker.getStockPrices(), stockPrices));

                    } catch (IOException ex) {
                        logger.error("Error occurred while trying to scrape data for tickerSymbols: " + missingTickersSymbols);
                    }
                }
            }
        }

        if (!missingStockPricesForTickers.isEmpty()) {
            saveTickers(missingStockPricesForTickers);
        }

        result.addAll(missingStockPricesForTickers.stream().map(tickerMapper::toDto).collect(Collectors.toList()));
    }

    private static void removeTickerSymbols(List<String> missingTickersSymbols, List<Ticker> missingStockPricesForTickers) {
        missingTickersSymbols.removeIf(ms -> !missingStockPricesForTickers.stream().filter(t -> t.getTickerSymbol().equals(ms)).collect(Collectors.toList()).isEmpty());
    }

    private static List<String> getSymbolsForMissingTickers(List<String> tickers, Set<Ticker> tickersWithStockPrices) {
        List<String> missingSymbols = tickers.stream()
                .filter(tickerSymbol ->
                        tickersWithStockPrices.stream()
                                .noneMatch(ticker -> ticker.getTickerSymbol().equals(tickerSymbol)))
                .collect(Collectors.toList());
        return missingSymbols;
    }

}
