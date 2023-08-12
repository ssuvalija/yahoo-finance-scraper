package com.yahoo.finace.scraper.repository;

import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM StockPrice sp WHERE sp.date < :cutoffDate")
    void deleteOlderThan(LocalDate cutoffDate);
}
