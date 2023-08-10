package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.Ticker;

import java.io.IOException;
import java.util.List;

public interface YahooScraperService {
    public List<Ticker> fetchData(List<String> tickerSymbols) throws IOException;
}
