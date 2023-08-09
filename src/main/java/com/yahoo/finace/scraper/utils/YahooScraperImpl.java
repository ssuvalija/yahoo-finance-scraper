package com.yahoo.finace.scraper.utils;
import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.now;

@Component
public class YahooScraperImpl implements YahooScraper {
    private static final String YAHOO_FINANCE_BASE_URL = "https://finance.yahoo.com/quote/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
    private static final Logger logger = LoggerFactory.getLogger(YahooScraperImpl.class);

    public List<Ticker> fetchData(List<String> tickerSymbols) throws IOException {
        List<Ticker> tickers = new ArrayList<>();
        for (String symbol : tickerSymbols) {
            Ticker ticker = scrapeTickerData(symbol);
            tickers.add(ticker);
        }
        return tickers;
    }

    private Ticker scrapeTickerData(String symbol) throws IOException {
        String profileUrl = mapSymbolsToProfileUrl(symbol);
        String summaryUrl = mapSymbolToSummaryUrl(symbol);

        Document profileDoc = connectToUrl(profileUrl);
        String companyTitle = extractCompanyTitle(profileDoc);
        String employeesNumber = removeCommaChar(extractEmployeesNumber(profileDoc));
        String yearFounded = extractYearFounded(profileDoc);

        Document summaryDoc = connectToUrl(summaryUrl);
        String marketCap = extractMarketCap(summaryDoc);
        String previousCloseValue = extractPreviousCloseValue(summaryDoc);
        String openValue = extractOpenValue(summaryDoc);

        return createTickerObject(symbol,
                companyTitle,
                employeesNumber,
                yearFounded,
                marketCap,
                previousCloseValue,
                openValue);
    }

    private Document connectToUrl(String url) throws IOException {
        return Jsoup.connect(url).userAgent(USER_AGENT).get();
    }

    private String extractCompanyTitle(Document doc) {
        return doc.select("[data-test=asset-profile]").select("h3").text();
    }

    private String extractEmployeesNumber(Document doc) {
        return doc.getElementsContainingOwnText("Full Time Employees").next().text();
    }

    private String extractYearFounded(Document doc) {
        String companyDescription = doc.select("section.quote-sub-section p").text();

        String yearRegex = "\\b(1\\d{3}|20\\d{2})\\b"; // Matches years between 1000 and 2099
        Pattern yearPattern = Pattern.compile(yearRegex);
        Matcher yearMatcher = yearPattern.matcher(companyDescription);
        String yearFounded = "N/A";
        if (yearMatcher.find()) {
            yearFounded = yearMatcher.group();
        }
        return yearFounded;
    }

    private String extractMarketCap(Document doc) {
        return doc.select("[data-test=MARKET_CAP-value]").text();
    }

    private String extractPreviousCloseValue(Document doc) {
        return doc.select("[data-test=PREV_CLOSE-value]").text();
    }

    private String extractOpenValue(Document doc) {
        return doc.select("[data-test=OPEN-value]").text();
    }

    private Ticker createTickerObject(String symbol, String companyTitle, String employeesNumber,
                                      String yearFounded, String marketCap, String previousCloseValue,
                                      String openValue) {
        Ticker ticker = new Ticker();
        ticker.setTickerSymbol(symbol);
        ticker.setMarketCap(marketCap);
        ticker.setCompanyName(companyTitle);
        try {
            ticker.setNumberOfEmployees(Integer.parseInt(employeesNumber));
        } catch (NumberFormatException e) {
            logger.error("Failed to parse numberOfEmployees: " + e.getMessage());
        }

        try {
            ticker.setYearFounded(Integer.parseInt(yearFounded));
        } catch (NumberFormatException e) {
            logger.error("Failed to parse yearFounded: " + e.getMessage());
        }

        StockPrice stockPrice = new StockPrice();
        String cleanOpenValue = removeCommaChar(openValue);
        stockPrice.setOpenPrice(new BigDecimal(cleanOpenValue));
        String cleanPreviousCloseValue = removeCommaChar(previousCloseValue);
        stockPrice.setPreviousClosePrice(new BigDecimal(cleanPreviousCloseValue));
        stockPrice.setDateTime(now());
        stockPrice.setTicker(ticker);

        ticker.setStockPrices(Collections.singletonList(stockPrice));

        return ticker;
    }

    private String removeCommaChar(String input) {
        return input.replace(String.valueOf(","), "");
    }
    private String mapSymbolToSummaryUrl(String tickerSymbol) {
        return mapSymbolToUrl(tickerSymbol, true);
    }

    private String mapSymbolsToProfileUrl(String tickerSymbol) {
        return mapSymbolToUrl(tickerSymbol, false);
    }

    private String mapSymbolToUrl(String tickerSymbol, boolean isSummaryUrl) {
        String url = YAHOO_FINANCE_BASE_URL + tickerSymbol;
        url = isSummaryUrl ? url : url + "/profile";
        return url;
    }
}
