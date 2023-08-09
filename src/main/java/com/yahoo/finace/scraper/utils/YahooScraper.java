package com.yahoo.finace.scraper.utils;

import com.yahoo.finace.scraper.model.Ticker;

import java.io.IOException;
import java.util.List;

public interface YahooScraper {
    public List<Ticker> fetchData(List<String> tickerSymbols) throws IOException;
}
