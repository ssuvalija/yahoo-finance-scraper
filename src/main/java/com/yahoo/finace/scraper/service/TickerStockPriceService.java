package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.repository.StockPriceRepository;
import com.yahoo.finace.scraper.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TickerStockPriceService {
    @Autowired
    private TickerRepository tickerRepository;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @Transactional
    public void saveTickerAndStockPrice(Ticker ticker, StockPrice stockPrice) {
        tickerRepository.save(ticker);
        stockPrice.setTicker(ticker);
        stockPriceRepository.save(stockPrice);
    }
}
