package com.yahoo.finace.scraper.service;

import com.yahoo.finace.scraper.model.StockPrice;
import com.yahoo.finace.scraper.model.Ticker;
import com.yahoo.finace.scraper.repository.StockPriceRepository;
import com.yahoo.finace.scraper.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TickerStockPriceServiceImpl implements TickerStockPriceService {
    @Autowired
    private TickerRepository tickerRepository;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @Transactional
    public void saveTickers(List<Ticker> tickers) {
        tickerRepository.saveAll(tickers);
    }
}
