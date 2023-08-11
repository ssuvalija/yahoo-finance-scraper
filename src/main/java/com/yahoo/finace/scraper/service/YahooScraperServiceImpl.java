package com.yahoo.finace.scraper.service;
import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YahooScraperServiceImpl implements YahooScraperService {
    private static final String YAHOO_FINANCE_BASE_URL = "https://finance.yahoo.com/quote/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
    private static final String CITY_KEY = "city";
    private static final String COUNTRY_KEY = "country";
    private static final String STATE_KEY = "state";
    private static final Logger logger = LoggerFactory.getLogger(YahooScraperServiceImpl.class);

    public List<Ticker> fetchData(List<String> tickerSymbols) throws IOException {
        List<Ticker> tickers = new ArrayList<>();
        for (String symbol : tickerSymbols) {
            Ticker ticker = scrapeTickerData(symbol);
            tickers.add(ticker);
        }
        return tickers;
    }

    public Ticker fetchData(String tickerSymbol) throws IOException {
        Ticker ticker = scrapeTickerData(tickerSymbol);
        return ticker;
    }

    private Ticker scrapeTickerData(String symbol) throws IOException {
        String profileUrl = mapSymbolsToProfileUrl(symbol);
        String summaryUrl = mapSymbolToSummaryUrl(symbol);

        Document profileDoc = connectToUrl(profileUrl);
        String companyTitle = extractCompanyTitle(profileDoc);
        String employeesNumber = extractEmployeesNumber(profileDoc);
        String yearFounded = extractYearFounded(profileDoc);
        Map<String, String> addressInfo = extractCityCountryAndState(profileDoc);
        String city = addressInfo.get(CITY_KEY);
        String state = addressInfo.get(STATE_KEY);
        String country = addressInfo.get(COUNTRY_KEY);
        boolean isMarketOpen = extractIsMarketOpen(profileDoc);

        Document summaryDoc = connectToUrl(summaryUrl);
        String marketCap = extractMarketCap(summaryDoc);
        String previousCloseValue = extractPreviousCloseValue(summaryDoc);
        String openValue = extractOpenValue(summaryDoc);

        return createTickerObject(symbol,
                companyTitle,
                employeesNumber,
                yearFounded,
                marketCap,
                city,
                country,
                state,
                previousCloseValue,
                openValue,
                isMarketOpen);
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
        return doc.select("[data-test=PREV_CLOSE-value]")
                .text();
    }

    private String extractOpenValue(Document doc) {
        return doc.select("[data-test=OPEN-value]").text();
    }

    public Map<String, String> extractCityCountryAndState(Document doc) {
        Map<String, String> result = new HashMap<>();

        Element locationElement = doc.select("div[data-test=qsp-profile] p").first();
        if (locationElement != null) {
            String locationText = locationElement.html();
            String[] lines = locationText.split("<br>");
            int cityIndex = 1;
            int countryIndex = 2;
            if (lines.length >= 5) {
                //Note: in some cases first address line takes first two lines
                if (lines.length == 6) {
                    cityIndex += 1;
                    countryIndex += 1;
                }
                String cityStateZip = lines[cityIndex];
                String country = lines[countryIndex];
                result.put(COUNTRY_KEY, country);

                String[] cityStateZipArray = cityStateZip.split(",");
                if (cityStateZipArray.length >= 2) {
                    String city = cityStateZipArray[0].trim();
                    String stateZip = cityStateZipArray[1].trim();

                    String[] stateZipArray = stateZip.split(" ");
                    result.put(CITY_KEY, city);
                    if (stateZipArray.length >= 2) {
                        String state = stateZipArray[0];
                        result.put(STATE_KEY, state);
                    }
                } else {
                    result.put(CITY_KEY, cityStateZip);
                }
            }
        }
        return result;
    }

    private boolean extractIsMarketOpen(Document doc) {
        String isMarketOpenText = doc.select("#quote-market-notice").text();
        return isMarketOpenText.endsWith("Market open.");
    }

    private Ticker createTickerObject(String symbol, String companyTitle, String employeesNumber,
                                      String yearFounded, String marketCap, String city,
                                      String country, String state, String previousCloseValue,
                                      String openValue, boolean isMarketOpen) {
        Ticker ticker = new Ticker();
        ticker.setTickerSymbol(symbol);
        ticker.setMarketCap(marketCap);
        ticker.setCompanyName(companyTitle);
        ticker.setCity(city);
        ticker.setCountry(country);
        ticker.setState(state);
        ticker.setNumberOfEmployees(parseInteger(employeesNumber));
        ticker.setYearFounded(parseInteger(yearFounded));

        StockPrice stockPrice = createStockPrice(ticker, previousCloseValue, openValue, isMarketOpen);
        ticker.setStockPrices(Collections.singletonList(stockPrice));

        return ticker;
    }

    private StockPrice createStockPrice(Ticker ticker, String previousCloseValue, String openValue, boolean isMarketOpen) {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setOpenPrice(parseDecimal(openValue));
        stockPrice.setPreviousClosePrice(parseDecimal(previousCloseValue));
        stockPrice.setDate(LocalDate.now());
        stockPrice.setTicker(ticker);
        stockPrice.setMarketOpen(isMarketOpen);
        return stockPrice;
    }
    private String removeCommaChar(String input) {
        return input.replace(",", "");
    }

    private int parseInteger(String value) {
        String cleanValue = removeCommaChar(value);
        try {
            return Integer.parseInt(cleanValue);
        } catch (NumberFormatException e) {
            logger.error("Failed to parse integer value: " + e.getMessage());
            return 0;
        }
    }

    private BigDecimal parseDecimal(String value) {
        String cleanValue = removeCommaChar(value);
        try {
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            logger.error("Failed to parse decimal value: " + e.getMessage());
            return BigDecimal.ZERO;
        }
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
