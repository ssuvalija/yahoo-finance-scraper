package com.yahoo.finace.scraper.repository;

import com.yahoo.finace.scraper.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {
    @Query(value = "SELECT * FROM ticker t where t.ticker_symbol = ?1", nativeQuery = true)
    public Ticker findByTickerSymbol(String symbol);

    @Query(value = "SELECT t.*\n" +
            "FROM ticker t\n" +
            "LEFT JOIN stock_price sp ON t.ticker_id = sp.ticker_id\n" +
            "WHERE t.ticker_symbol IN :tickers\n" +
            "    AND sp.date_time >= :date\n" +
            "    AND sp.date_time < DATE_ADD(:date, INTERVAL 1 DAY);", nativeQuery = true)
    public Set<Ticker> getTickersWithStockPrices(
            @Param("tickers") List<String> tickers,
            @Param("date") LocalDate date);

}
