package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.Ticker;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockPriceDownloaderImpl implements StockPriceDownloader {
    private static final String CSV_URL_FORMAT = "https://query1.finance.yahoo.com/v7/finance/download/%s" +
            "?period1=%d&period2=%d&interval=1d&events=history&includeAdjustedClose=true";

    public List<StockPrice> downloadAndMapStockPrices(Ticker ticker, LocalDate startDate, LocalDate endDate) throws IOException {
            long startDateTimestamp = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
            long endDateTimestamp = endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);

            // Create the URL
            String csvUrl = String.format(CSV_URL_FORMAT, ticker.getTickerSymbol(), startDateTimestamp, endDateTimestamp);

            // Initialize HttpClient
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(URI.create(csvUrl));
                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String csvData = EntityUtils.toString(entity);
                        return parseCsvAndMapToStockPrices(csvData, ticker);
                    }
                }
            }
            return new ArrayList<>(); // Return an empty list if data couldn't be downloaded
    }

    public List<StockPrice> downloadAndMapStockPrices(Ticker ticker, LocalDate currentDate) throws IOException {
        // By default, we will fetch the data for the past 5 years
        return downloadAndMapStockPrices(ticker, currentDate.minusYears(5).minusDays(1), currentDate);
    }

    private static List<StockPrice> parseCsvAndMapToStockPrices(String csvData, Ticker ticker) {
        List<StockPrice> stockPrices = new ArrayList<>();
        String[] lines = csvData.split("\\r?\\n");

        BigDecimal previousClose = null; // Initialize the previous close value

        for (int i = 1; i < lines.length; i++) { // Skip header line
            String[] columns = lines[i].split(",");
            LocalDate dateTime = LocalDate.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            BigDecimal open;
            try {
                open = new BigDecimal(columns[1]);
            } catch (NumberFormatException | NullPointerException e) {
                open = BigDecimal.ZERO;
            }

            BigDecimal close;
            try {
                close = new BigDecimal(columns[4]);
            } catch (NumberFormatException | NullPointerException e) {
                close = BigDecimal.ZERO;
            }

            if (previousClose != null) { // Skip the first row
                StockPrice stockPrice = new StockPrice();
                stockPrice.setDate(dateTime);
                stockPrice.setMarketOpen(true);
                stockPrice.setOpenPrice(open);
                stockPrice.setPreviousClosePrice(previousClose);
                stockPrice.setTicker(ticker);

                stockPrices.add(stockPrice);
            }

            previousClose = close;
        }

        return stockPrices;
    }

}