package com.yahoo.finance.scraper.service;

import com.yahoo.finance.scraper.model.Ticker;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public interface YahooScraperService {
    List<Ticker> fetchData(List<String> tickerSymbols);
    Ticker fetchData(String tickerSymbol) throws IOException;
    public Ticker getTickerData(String symbol, Document profileDoc, Document summaryDoc);
}
