package com.yahoo.finace.scraper.service;

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

    public List<StockPrice> downloadAndMapStockPrices(String tickerSymbol, LocalDate currentDate) throws IOException {
        // Calculate dates: current date and date before 5 years
        long currentTimestamp = currentDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long fiveYearsAgoTimestamp = currentDate.minusYears(5).atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        // Create the URL
        String csvUrl = String.format(CSV_URL_FORMAT, tickerSymbol, fiveYearsAgoTimestamp, currentTimestamp);

        // Initialize HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(URI.create(csvUrl));
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String csvData = EntityUtils.toString(entity);
                    return parseCsvAndMapToStockPrices(csvData);
                }
            }
        }
        return new ArrayList<>(); // Return an empty list if data couldn't be downloaded
    }

    private static List<StockPrice> parseCsvAndMapToStockPrices(String csvData) {
        List<StockPrice> stockPrices = new ArrayList<>();
        String[] lines = csvData.split("\\r?\\n");
        for (int i = 1; i < lines.length; i++) { // Skip header line
            String[] columns = lines[i].split(",");
            LocalDate dateTime = LocalDate.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            BigDecimal open = new BigDecimal(columns[1]);
            BigDecimal close = new BigDecimal(columns[4]);

            StockPrice stockPrice = new StockPrice();
            stockPrice.setDate(dateTime);
            stockPrice.setMarketOpen(true);
            stockPrice.setOpenPrice(open);
            stockPrice.setPreviousClosePrice(close);

            stockPrices.add(stockPrice);
        }
        return stockPrices;
    }
}