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
    Ticker findByTickerSymbol(String symbol);

    @Query("SELECT t FROM Ticker t LEFT JOIN FETCH t.stockPrices sp " +
            "WHERE t.tickerSymbol IN :tickers AND sp.date = :date")
    Set<Ticker> getTickersWithStockPrices(
            @Param("tickers") List<String> tickers,
            @Param("date") LocalDate date);

    @Query("SELECT t FROM Ticker t  " +
            "WHERE t.tickerSymbol IN :tickers")
    List<Ticker> getTickers(@Param("tickers") List<String> tickers);

}
