package com.yahoo.finace.scraper.repository;

import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    @Query(value =
            "SELECT t.ticker_id, t.ticker_symbol, t.company_name, t.market_cap, t.year_founded,\n" +
            "t.city, t.state, sp.stock_price_id, sp.date_time, sp.previous_close_price, sp.open_price\n" +
            "FROM ticker t\n" +
            "INNER JOIN stock_price sp ON t.ticker_id = sp.ticker_id\n" +
            "WHERE t.ticker_symbol IN (:tickers) -- List of ticker symbols\n" +
            "  AND sp.date_time >= :date -- Desired date\n" +
            "  AND sp.date_time < DATE_ADD(:date, INTERVAL 1 DAY) -- Next day", nativeQuery = true)
    public List<StockPrice> findStockPricesForTickersOnDate(
            @Param("tickers") List<String> tickers,
            @Param("date") LocalDate date);
}
