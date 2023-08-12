package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.Ticker;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public interface YahooScraperService {
    List<Ticker> fetchData(List<String> tickerSymbols) throws IOException;
    Ticker fetchData(String tickerSymbol) throws IOException;
    public Ticker getTickerData(String symbol, Document profileDoc, Document summaryDoc);
}
