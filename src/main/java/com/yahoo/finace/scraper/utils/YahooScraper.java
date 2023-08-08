package com.yahoo.finace.scraper.utils;
import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.service.TickerStockPriceService;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.now;

@Component
public class YahooScraper {
    private static final String YAHOO_FINANCE_BASE_URL = "https://finance.yahoo.com/quote/";
    @Autowired
    private TickerStockPriceService tickerStockPriceService;

    public void fetchData(List<String> tickerSymbols) throws IOException {

        for (String symbol: tickerSymbols) {
            String profileUrl = mapSymbolsToProfileUrl(symbol);
            String summaryUrl = mapSymbolToSummaryUrl(symbol);
            Document doc = Jsoup.connect(profileUrl).get();
            String companyTitle = doc.select("[data-test=asset-profile]").select("h3").text();
            String employeesNumber = doc.getElementsContainingOwnText("Full Time Employees").next().text();

            String companyDescription = doc.select("section:contains(Description)").text();

            String yearRegex = "\\b(1\\d{3}|20\\d{2})\\b"; // Matches years between 1000 and 2099
            Pattern yearPattern = Pattern.compile(yearRegex);
            Matcher yearMatcher = yearPattern.matcher(companyDescription);
            String yearFounded = "N/A";
            if (yearMatcher.find()) {
                yearFounded = yearMatcher.group();
            }

            Document doc2 = Jsoup.connect(summaryUrl).get();
            String marketCap = doc2.select("[data-test=MARKET_CAP-value]").text();
            String previousCloseValue = doc2.select("[data-test=PREV_CLOSE-value]").text();
            String openValue = doc2.select("[data-test=OPEN-value]").text();

            /*https://query1.finance.yahoo.com/v7/finance/download/KO?period1=1659844943&period2=1691380943&interval=1d&events=history&includeAdjustedClose=true*/

            System.out.println(companyTitle);
            System.out.println(employeesNumber);
            System.out.println(yearFounded);
            System.out.println(marketCap);
            System.out.println(previousCloseValue);
            System.out.println(openValue);


            Ticker ticker = new Ticker();
            ticker.setTickerSymbol(symbol);
            ticker.setMarketCap(marketCap);
            ticker.setCompanyName(companyTitle);
            ticker.setYearFounded(Integer.parseInt(yearFounded));

            StockPrice stockPrice = new StockPrice();
            stockPrice.setOpenPrice(Double.parseDouble(openValue));
            stockPrice.setPreviousClosePrice(Double.parseDouble(previousCloseValue));
            stockPrice.setDateTime(now());

            tickerStockPriceService.saveTickerAndStockPrice(ticker, stockPrice);


        }
    }

    public static String mapSymbolToSummaryUrl(String tickerSymbol) {
        return mapSymbolToUrl(tickerSymbol, true);
    }

    public static String mapSymbolsToProfileUrl(String tickerSymbol) {
        return mapSymbolToUrl(tickerSymbol, false);
    }

    public static String mapSymbolToUrl(String tickerSymbol, boolean isSummaryUrl) {
        String url = YAHOO_FINANCE_BASE_URL + tickerSymbol;
        url = isSummaryUrl ? url : url + "/profile";
        return url;
    }

}
